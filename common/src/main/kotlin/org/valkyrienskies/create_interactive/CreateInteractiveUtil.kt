package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.Contraption
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Block
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.ShipTeleportData
import org.valkyrienskies.core.apigame.world.properties.DimensionId
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.create_interactive.mixin.ContraptionRotationStateAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.yRange
import java.lang.ref.WeakReference

object CreateInteractiveUtil {
    fun createShipForContraption(level: ServerLevel, contraption: Contraption, blockPos: BlockPos): ShipId {
        // Try adding the rigid body of this entity from the world
        val serverShip: ServerShip = level.shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, level.dimensionId)

        // Anchor at ship center
        val shipCenter: Vector3ic = serverShip.chunkClaim.getCenterBlockCoordinates(level.yRange, Vector3i())

        for ((pos, value) in contraption.blocks) {
            // TODO: Do I need to sub this???
            val localPos = pos // .subtract(contraption.anchor)
            val newPos = localPos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z())
            val flags =
                Block.UPDATE_MOVE_BY_PISTON or Block.UPDATE_SUPPRESS_DROPS or Block.UPDATE_KNOWN_SHAPE or Block.UPDATE_CLIENTS or Block.UPDATE_IMMEDIATE
            level.setBlock(newPos, value.state, flags)
        }

        serverShip.isStatic = true

        return serverShip.id
    }

    fun doesContraptionHaveShipLoaded(contraption: Contraption): Boolean {
        val contraptionEntity: AbstractContraptionEntity = contraption.entity ?: return false
        val shipId = (contraptionEntity as AbstractContraptionEntityDuck).getShadowShipId() ?: return false
        return contraptionEntity.level.shipObjectWorld.loadedShips.getById(shipId) != null
    }

    private fun posRotToShipTransform(contraptionPosRot: ContraptionPosRot, serverShip: ServerShip, level: ServerLevel): ShipTransform {
        val (contraptionPos, contraptionRot) = contraptionPosRot

        // Anchor at ship center of mass
        val cmInShip: Vector3dc = serverShip.inertiaData.centerOfMassInShip
        val shipCenter: Vector3ic = serverShip.chunkClaim.getCenterBlockCoordinates(level.yRange, Vector3i())
        val offset = cmInShip.sub(
            shipCenter.x().toDouble(),
            shipCenter.y().toDouble(),
            shipCenter.z().toDouble(),
            Vector3d()
        )
        contraptionRot.transform(offset)
        val newPos: Vector3dc = contraptionPos.add(offset, Vector3d())
        val newScale = 1.0
        val posInShip: Vector3dc = cmInShip.add(0.5, 0.5, 0.5, Vector3d())
        return ShipTransformImpl(
            newPos,
            posInShip,
            contraptionPosRot.rot,
            Vector3d(newScale)
        )
    }

    fun teleportShipToPosRot(contraptionPosRot: ContraptionPosRot, serverShip: ServerShip, level: ServerLevel) {
        val shipTransform = posRotToShipTransform(contraptionPosRot, serverShip, level)
        val newVel: Vector3dc = Vector3d()
        val newOmega: Vector3dc = Vector3d()
        val newDimension: String = level.dimensionId
        // Because of an issue with the teleport function we have to set the center of mass to be cmInShip + (.5,.5,.5)
        val shipTeleportData: ShipTeleportData = ShipTeleportDataImplFixed(
            shipTransform.positionInWorld, shipTransform.positionInShip, shipTransform.shipToWorldRotation, newVel, newOmega, newDimension, shipTransform.shipToWorldScaling.x()
        )
        level.shipObjectWorld.teleportShip(serverShip, shipTeleportData)
    }

    fun updateShipShadow(entity: AbstractContraptionEntity) {
        val shadowShipId = (entity as AbstractContraptionEntityDuck).getShadowShipId() ?: return
        val level = entity.level
        if (level.isClientSide) {
            return
        }
        val contraptionPosRot = getContraptionPosRot(entity)
        val serverShip: ServerShip? = (level as ServerLevel).shipObjectWorld.allShips.getById(shadowShipId)
        if (serverShip != null) {
            serverShip.transformProvider = object: ServerShipTransformProvider {
                override fun provideNextTransform(
                    prevShipTransform: ShipTransform,
                    shipTransform: ShipTransform
                ): ShipTransform {
                    return posRotToShipTransform(contraptionPosRot, serverShip, level)
                }
            }

            // Make the ship static, so it won't be affected by physics
            serverShip.isStatic = true
        } else {
            // Somehow the ship died?
            println("Somehow a contraption ship shadow died!")
            (entity as AbstractContraptionEntityDuck).shadowShipId = null
        }
    }

    data class ContraptionPosRot(val pos: Vector3dc, val rot: Quaterniondc)

    fun getContraptionPosRot(entity: AbstractContraptionEntity): ContraptionPosRot {
        val rotationStateOriginal = AbstractContraptionEntity::class.java.cast(entity).rotationState
        val rotationState = rotationStateOriginal as ContraptionRotationStateAccessor
        val newRot = Quaterniond().rotateZYX(
            Math.toRadians(rotationState.getZRotation().toDouble()),
            Math.toRadians(rotationState.getYRotation().toDouble()),
            Math.toRadians(rotationState.getXRotation().toDouble()),
        )
        newRot.rotateLocalY(Math.toRadians(rotationStateOriginal.yawOffset.toDouble()))

        // Train on a train!!!
        val parentShip = entity.level.getShipManagingPos(entity.position())
        if (parentShip != null) {
            val newNewPos = parentShip.transform.shipToWorld.transformPosition(entity.anchorVec.toJOML().add(0.5, 0.5, 0.5), Vector3d())
            val newNewRot = parentShip.transform.shipToWorldRotation.mul(newRot, Quaterniond())
            return ContraptionPosRot(newNewPos, newNewRot)
        }

        return ContraptionPosRot(entity.anchorVec.toJOML().add(0.5, 0.5, 0.5), newRot)
    }

    private val shipIdToContraptionEntityClientInternal: MutableMap<ShipId, WeakReference<AbstractContraptionEntity>> = HashMap()
    private val shipIdToContraptionEntityServerInternal: MutableMap<ShipId, WeakReference<AbstractContraptionEntity>> = HashMap()

    val shipIdToContraptionEntityClient: Map<ShipId, WeakReference<AbstractContraptionEntity>>
        get() = shipIdToContraptionEntityClientInternal

    val shipIdToContraptionEntityServer: Map<ShipId, WeakReference<AbstractContraptionEntity>>
        get() = shipIdToContraptionEntityServerInternal

    fun linkShipToContraption(shipId: ShipId, contraptionEntity: AbstractContraptionEntity) {
        if (contraptionEntity.level.isClientSide) {
            shipIdToContraptionEntityClientInternal[shipId] = WeakReference(contraptionEntity)
        } else {
            shipIdToContraptionEntityServerInternal[shipId] = WeakReference(contraptionEntity)
        }
    }

    data class ShipTeleportDataImplFixed(
        override val newPos: Vector3dc = Vector3d(),
        val newPosInShip: Vector3dc = Vector3d(),
        override val newRot: Quaterniondc = Quaterniond(),
        override val newVel: Vector3dc = Vector3d(),
        override val newOmega: Vector3dc = Vector3d(),
        override val newDimension: DimensionId? = null,
        override val newScale: Double? = null,
    ) : ShipTeleportData {
        override fun createNewShipTransform(oldShipTransform: ShipTransform): ShipTransform = ShipTransformImpl(
            positionInWorld = newPos,
            positionInShip = newPosInShip,
            shipToWorldRotation = newRot,
            shipToWorldScaling = newScale?.let { Vector3d(it) } ?: oldShipTransform.shipToWorldScaling,
        )
    }
}
