package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.ShipTeleportData
import org.valkyrienskies.core.apigame.world.properties.DimensionId
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.create_interactive.mixin.ContraptionRotationStateAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck
import org.valkyrienskies.create_interactive.mixinducks.ContraptionRotationStateDuck
import org.valkyrienskies.create_interactive.mixinducks.OrientedContraptionEntityDuck
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.settings
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.yRange
import java.lang.ref.WeakReference

object CreateInteractiveUtil {
    fun createShipForContraption(level: ServerLevel, contraption: Contraption, blockPos: BlockPos): ShipId? {
        if (contraption.javaClass.packageName.contains("createbigcannons")) {
            // Do not create shadow ships for CBC, too hard
            return null
        }
        // Try adding the rigid body of this entity from the world
        val serverShip: ServerShip = level.shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, level.dimensionId)

        // Anchor at ship center
        val shipCenter: Vector3ic = serverShip.getChunkClaimCenterPos(level)

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
        val shipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return false
        return contraptionEntity.level.shipObjectWorld.loadedShips.getById(shipId) != null
    }

    private fun posRotToShipTransform(contraptionPosRot: ContraptionPosRot, serverShip: ServerShip, level: ServerLevel): ShipTransform {
        val (contraptionPos, contraptionRot) = contraptionPosRot

        // Anchor at ship center of mass
        val cmInShip: Vector3dc = serverShip.inertiaData.centerOfMassInShip
        val shipCenter: Vector3ic = serverShip.getChunkClaimCenterPos(level)
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

    fun updateShipShadow(entity: AbstractContraptionEntity, serverShip: ServerShip, posRot: ContraptionPosRot): ShipTransform {
        val transform = posRotToShipTransform(posRot, serverShip, entity.level as ServerLevel)
        serverShip.transformProvider = object: ServerShipTransformProvider {
            override fun provideNextTransform(
                prevShipTransform: ShipTransform,
                shipTransform: ShipTransform
            ): ShipTransform? {
                if (entity is CarriageContraptionEntity && isTrainDerailed(entity)) {
                    return null
                }
                return transform
            }
        }

        // If the ship is in the wrong dimension then teleport it
        if (entity.level.dimensionId != serverShip.chunkClaimDimension) {
            teleportShipToPosRot(posRot, serverShip, entity.level as ServerLevel)
        }

        // Make the ship static, so it won't be affected by physics
        serverShip.isStatic = true
        // Don't let the ship teleport through dimensions on its own
        serverShip.settings.changeDimensionOnTouchPortals = false

        return transform
    }

    fun moveContraptionToTransform(entity: CarriageContraptionEntity, ship: Ship) {
        val shipTransform = ship.transform
        val angles: Vector3dc = shipTransform.shipToWorldRotation.getEulerAnglesZYX(Vector3d())

        val rotState = AbstractContraptionEntity.ContraptionRotationState()
        rotState as ContraptionRotationStateAccessor
        rotState.setXRotation(Math.toDegrees(angles.x()).toFloat())
        rotState.setYRotation(Math.toDegrees(angles.y()).toFloat())
        rotState.setZRotation(Math.toDegrees(angles.z()).toFloat())
        (entity as OrientedContraptionEntityDuck).`ci$setForcedRotation`(rotState)

        // Anchor at ship center of mass
        val shipCenter: Vector3ic = ship.getChunkClaimCenterPos(entity.level)
        val newPos: Vector3dc = shipTransform.shipToWorld.transformPosition(Vector3d(shipCenter).add(0.5, 0.5, 0.5))
        // Add (.5, 0, .5) to compensate for the anchorVec offset
        entity.setPos(newPos.x(), newPos.y() - 0.5, newPos.z())
    }

    data class ContraptionPosRot(val pos: Vector3dc, val rot: Quaterniondc)

    fun getContraptionPosRot(entity: AbstractContraptionEntity): ContraptionPosRot {
        val rotationStateOriginal = AbstractContraptionEntity::class.java.cast(entity).rotationState
        val newRot = (rotationStateOriginal as ContraptionRotationStateDuck).`ci$getRotationQuaternion`(Quaterniond())
        val contraptionPos: Vector3dc = entity.anchorVec.toJOML().add(0.5, 0.5, 0.5)

        // Train on a train!!!
        val parentShip = entity.level.getShipManagingPos(entity.position())
        if (parentShip != null) {
            val newNewPos = parentShip.transform.shipToWorld.transformPosition(contraptionPos, Vector3d())
            val newNewRot = parentShip.transform.shipToWorldRotation.mul(newRot, Quaterniond())
            return ContraptionPosRot(newNewPos, newNewRot)
        }

        return ContraptionPosRot(contraptionPos, newRot)
    }

    fun getContraptionPosRotForRender(entity: AbstractContraptionEntity, partialTick: Double): ContraptionPosRot {
        val prevRot: Quaterniondc = ((entity as AbstractContraptionEntityDuck).`ci$getPrevTickRotationState`() as ContraptionRotationStateDuck).`ci$getRotationQuaternion`(Quaterniond())
        val curRot: Quaterniondc = (AbstractContraptionEntity::class.java.cast(entity).rotationState as ContraptionRotationStateDuck).`ci$getRotationQuaternion`(Quaterniond())
        val newRot = prevRot.slerp(curRot, partialTick, Quaterniond()).normalize()
        val contraptionPos: Vector3dc = Vector3d(
            entity.anchorVec.x * partialTick + entity.prevAnchorVec.x * (1.0 - partialTick),
            entity.anchorVec.y * partialTick + entity.prevAnchorVec.y * (1.0 - partialTick),
            entity.anchorVec.z * partialTick + entity.prevAnchorVec.z * (1.0 - partialTick),
        ).add(0.5, 0.5, 0.5)

        // Train on a train!!!
        val parentShip = entity.level.getShipManagingPos(entity.position()) as ClientShip?
        if (parentShip != null) {
            val newNewPos = parentShip.renderTransform.shipToWorld.transformPosition(contraptionPos, Vector3d())
            val newNewRot = parentShip.renderTransform.shipToWorldRotation.mul(newRot, Quaterniond()).normalize()
            return ContraptionPosRot(newNewPos, newNewRot)
        }

        return ContraptionPosRot(contraptionPos, newRot)
    }

    fun getContraptionPosRot(entity: AbstractContraptionEntity, parentTransform: ShipTransform?): ContraptionPosRot {
        val rotationStateOriginal = AbstractContraptionEntity::class.java.cast(entity).rotationState
        val newRot = (rotationStateOriginal as ContraptionRotationStateDuck).`ci$getRotationQuaternion`(Quaterniond()).normalize()

        if (parentTransform != null) {
            val newNewPos = parentTransform.shipToWorld.transformPosition(
                entity.anchorVec.toJOML().add(0.5, 0.5, 0.5),
                Vector3d()
            )
            val newNewRot = parentTransform.shipToWorldRotation.mul(newRot, Quaterniond()).normalize()
            return ContraptionPosRot(newNewPos, newNewRot)
        }

        return ContraptionPosRot(entity.anchorVec.toJOML().add(0.5, 0.5, 0.5), newRot)
    }

    fun getShipForMovementContext(context: MovementContext): Ship? = getShipForContraption(context.contraption)

    private fun getShipForContraption(contraption: Contraption): Ship? {
        val contraptionEntity = contraption.entity ?: return null
        val shadowShipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return null
        return contraptionEntity.level.shipObjectWorld.allShips.getById(shadowShipId)
    }

    fun getActorAtPos(level: Level, pos: BlockPos): CreateActorImmutable? {
        val ship = level.getShipManagingPos(pos) ?: return null
        val contraptionEntityWeakReference =
            shipIdToContraptionEntityClient[ship.id] ?: return null
        val contraptionEntity = contraptionEntityWeakReference.get() ?: return null

        // Anchor at ship center
        val shipCenter: Vector3ic = ship.getChunkClaimCenterPos(level)
        val relativePos = pos.subtract(shipCenter.toBlockPos())

        return (contraptionEntity.contraption as ContraptionDuck).`ci$getActorAtPos`(relativePos)
    }

    fun Ship.getChunkClaimCenterPos(level: Level): Vector3ic =
        chunkClaim.getCenterBlockCoordinates(level.yRange, Vector3i())

    private val shipIdToContraptionEntityClientInternal: MutableMap<ShipId, WeakReference<AbstractContraptionEntity>> = HashMap()
    private val shipIdToContraptionEntityServerInternal: MutableMap<ShipId, WeakReference<AbstractContraptionEntity>> = HashMap()

    val shipIdToContraptionEntityClient: Map<ShipId, WeakReference<AbstractContraptionEntity>>
        get() = shipIdToContraptionEntityClientInternal

    val shipIdToContraptionEntityServer: Map<ShipId, WeakReference<AbstractContraptionEntity>>
        get() = shipIdToContraptionEntityServerInternal

    fun getContraptionEntityForShip(shipId: ShipId, clientSide: Boolean): AbstractContraptionEntity? {
        return if (clientSide) {
            shipIdToContraptionEntityClient[shipId]?.get()
        } else {
            shipIdToContraptionEntityServer[shipId]?.get()
        }
    }

    fun linkShipToContraption(shipId: ShipId, contraptionEntity: AbstractContraptionEntity) {
        if (contraptionEntity.level.isClientSide) {
            shipIdToContraptionEntityClientInternal[shipId] = WeakReference(contraptionEntity)
        } else {
            shipIdToContraptionEntityServerInternal[shipId] = WeakReference(contraptionEntity)
        }
    }

    fun unlinkShipToContraption(shipId: ShipId, contraptionEntity: AbstractContraptionEntity) {
        if (contraptionEntity.level.isClientSide) {
            val prevVal = shipIdToContraptionEntityClientInternal[shipId]?.get()
            if (prevVal != null && prevVal == contraptionEntity) {
                shipIdToContraptionEntityClientInternal.remove(shipId)
            }
        } else {
            val prevVal = shipIdToContraptionEntityServerInternal[shipId]?.get()
            if (prevVal != null && prevVal == contraptionEntity) {
                shipIdToContraptionEntityServerInternal.remove(shipId)
            }
        }
    }

    internal fun onShipUnloadEventClient(clientShip: ClientShip) {
        shipIdToContraptionEntityClientInternal.remove(clientShip.id)
    }

    fun getBlockEntity(context: MovementContext): BlockEntity? {
        val ship = getShipForMovementContext(context) ?: return null
        val shipCenter: Vector3ic = ship.getChunkClaimCenterPos(context.world)
        val blockPos = context.localPos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z())
        return context.world.getBlockEntity(blockPos)
    }

    fun isTrainDerailed(carriageEntity: CarriageContraptionEntity): Boolean {
        return carriageEntity.carriage?.train?.derailed == true
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
