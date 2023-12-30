package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.BlockMovementChecks
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.StructureTransform
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import com.simibubi.create.content.trains.entity.TrainRelocator
import com.simibubi.create.content.trains.track.ITrackBlock
import com.simibubi.create.content.trains.track.TrackBlock
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import net.minecraft.world.phys.Vec3
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3i
import org.joml.Vector3ic
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.ServerShipTransformProvider
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.ShipTeleportData
import org.valkyrienskies.core.apigame.world.properties.DimensionId
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.core.util.expand
import org.valkyrienskies.create_interactive.mixin.CarriageBogeyAccessor
import org.valkyrienskies.create_interactive.mixin.DimensionalCarriageEntityAccessor
import org.valkyrienskies.create_interactive.mixin.TrainAccessor
import org.valkyrienskies.create_interactive.mixin_logic.MixinTrainLogic.getLocationVec3i
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck
import org.valkyrienskies.create_interactive.mixinducks.ContraptionRotationStateDuck
import org.valkyrienskies.create_interactive.mixinducks.OrientedContraptionEntityDuck
import org.valkyrienskies.create_interactive.services.NoOptimize
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.set
import org.valkyrienskies.mod.common.util.settings
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.yRange
import java.lang.ref.WeakReference
import kotlin.math.round
import kotlin.math.roundToInt

object CreateInteractiveUtil {
    fun createShipForContraption(level: ServerLevel, contraption: Contraption, blockPos: BlockPos, blocks: Map<BlockPos, StructureTemplate.StructureBlockInfo> = contraption.blocks): ShipId? {
        if (contraption.javaClass.packageName.contains("createbigcannons")) {
            // Do not create shadow ships for CBC, too hard
            return null
        }
        // Try adding the rigid body of this entity from the world
        val serverShip: ServerShip = level.shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, level.dimensionId)

        // Anchor at ship center
        val shipCenter: Vector3ic = serverShip.getChunkClaimCenterPos(level)

        // Order the blocks such that we place non-brittle blocks first, then brittle blocks (Inspired by Contraption.addBlocksToWorld())
        val nonBrittleBlocks = blocks.entries.filter { !BlockMovementChecks.isBrittle(it.value.state) }
        val brittleBlocks = blocks.entries.filter { BlockMovementChecks.isBrittle(it.value.state) }
        val blocksOrderedCorrectly = nonBrittleBlocks + brittleBlocks

        for ((pos, structureInfo) in blocksOrderedCorrectly) {
            val newPos = pos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z())

            val flags = Block.UPDATE_MOVE_BY_PISTON or Block.UPDATE_ALL
            level.setBlock(newPos, structureInfo.state, flags)

            // region Copy the tile entity to the ship
            val newBlockEntity = level.getBlockEntity(newPos)
            if (newBlockEntity != null) {
                // Transform the block entity, put it in the ship
                val tag: CompoundTag? = structureInfo.nbt
                if (tag != null) {
                    tag.putInt("x", newPos.x)
                    tag.putInt("y", newPos.y)
                    tag.putInt("z", newPos.z)
                    if (newBlockEntity is IMultiBlockEntityContainer && tag.contains("LastKnownPos")) tag.put(
                        "LastKnownPos", NbtUtils.writeBlockPos(
                            BlockPos.ZERO.below(
                                Int.MAX_VALUE - 1
                            )
                        )
                    )
                    newBlockEntity.load(tag)
                    level.setBlockEntity(newBlockEntity)
                }
            }
            // endregion
        }

        attemptTrainRelocation(level, contraption.anchor, blocks, shipCenter)

        serverShip.isStatic = true
        return serverShip.id
    }

    private fun createTrackAABB(level: ServerLevel, offsetPos: BlockPos, localBlocks: Map<BlockPos, StructureTemplate.StructureBlockInfo>, shipCenter: Vector3ic): AABBdc? {
        var minPosNotRelative: Vector3i? = null
        var maxPosNotRelative: Vector3i? = null
        val posAsJOML = Vector3i()
        val random = RandomSource.create()
        for ((pos, structureInfo) in localBlocks) {
            if (structureInfo.state.block is ITrackBlock) {
                // Tick the track block to create its track graph immediately (normally create waits until the next tick, but that's too slow for us)
                val posInWorld = pos.offset(offsetPos)
                val posInShip = pos.offset(shipCenter.toBlockPos())
                val stateInWorld = level.getBlockState(posInShip)
                val block = stateInWorld.block
                if (block is TrackBlock) {
                    block.tick(stateInWorld, level, posInShip, random)
                }
                posAsJOML.set(posInWorld)
                if (minPosNotRelative == null) {
                    minPosNotRelative = Vector3i(posAsJOML)
                } else {
                    minPosNotRelative.min(posAsJOML)
                }
                if (maxPosNotRelative == null) {
                    maxPosNotRelative = Vector3i(posAsJOML)
                } else {
                    maxPosNotRelative.max(posAsJOML)
                }
            }
        }
        if (minPosNotRelative == null || maxPosNotRelative == null) return null
        return AABBd(minPosNotRelative.x().toDouble(), minPosNotRelative.y().toDouble(), minPosNotRelative.z().toDouble(), maxPosNotRelative.x().toDouble() + 1.0, maxPosNotRelative.y().toDouble() + 1.0, maxPosNotRelative.z().toDouble() + 1.0).expand(1.0)
    }

    internal fun attemptTrainRelocation(level: ServerLevel, offsetPos: BlockPos, localBlocks: Map<BlockPos, StructureTemplate.StructureBlockInfo>, shipCenter: Vector3ic, transform: StructureTransform? = null) {
        if (localBlocks.isEmpty()) return

        val searchAABB = createTrackAABB(level, offsetPos, localBlocks, shipCenter) ?: return
        val searchAABBmc = searchAABB.toMinecraft()
        val trainCars = level.getEntitiesOfClass(CarriageContraptionEntity::class.java, searchAABBmc)

        // Only attempt to relocate the first carriage of trains that aren't derailed. Check the bounding box twice to avoid entities VS adds to this query.
        trainCars.filter { !it.carriage.train.derailed && it.carriageIndex == it.carriage.train.carriages.size - 1 && it.boundingBox.intersects(searchAABBmc) }.forEach { carriageEntity ->
            val leadingPoint = carriageEntity.carriage.leadingPoint ?: return@forEach

            val node1Location: Vector3ic = leadingPoint.node1?.location?.getLocationVec3i() ?: return@forEach
            val node2Location: Vector3ic = leadingPoint.node2?.location?.getLocationVec3i() ?: return@forEach

            val normalLocal: Vector3dc = Vector3d(node1Location.sub(node2Location, Vector3i())).mul(-1.0).normalize()

            val normal: Vec3 = if (transform == null) {
                Vector3d(node1Location.sub(node2Location, Vector3i())).mul(-1.0).normalize().toMinecraft()
            } else {
                val diff = Vector3d(node1Location.sub(node2Location, Vector3i()))
                transform.applyWithoutOffsetUncentered(diff.mul(-1.0).toMinecraft()).normalize()
            }

            val bogey = carriageEntity.carriage.trailingBogey()
            val bogeyRelPos =
                if ((bogey as CarriageBogeyAccessor).getIsLeading()) BlockPos.ZERO else Vector3d(normalLocal).mul(-carriageEntity.carriage.bogeySpacing.toDouble())
                    .let { BlockPos(it.x.roundToInt(), it.y.roundToInt(), it.z.roundToInt()) }

            val leadingBogeyPosInLocal: Vector3dc = carriageEntity.anchorVec.toJOML()
                .add(bogeyRelPos.x.toDouble(), bogeyRelPos.y.toDouble(), bogeyRelPos.z.toDouble()).sub(0.0, 1.0, 0.0)
            val closestBlockPosRelative = BlockPos(
                leadingBogeyPosInLocal.x().roundToInt(),
                leadingBogeyPosInLocal.y().roundToInt(),
                leadingBogeyPosInLocal.z().roundToInt(),
            ).subtract(offsetPos)

            var success = false
            if (localBlocks[closestBlockPosRelative]?.state?.block is ITrackBlock) {
                // Relocate it!
                val defaultOne = closestBlockPosRelative.offset(shipCenter.x(), shipCenter.y(), shipCenter.z())
                // Subtract the normal to prevent the train from moving
                val withTransformPos = transform?.apply(closestBlockPosRelative)
                val relocatePos = (withTransformPos ?: defaultOne).offset(-round(normal.x()), -round(normal.y()), -round(normal.z()))
                success = TrainRelocator.relocate(carriageEntity.carriage.train, level, relocatePos, null, false, normal, false)
                if (success) {
                    carriageEntity.moveTo(carriageEntity.carriage.getDimensional(level).positionAnchor)
                }
            }
            if (!success) {
                // Derail
                val train = carriageEntity.carriage.train
                (train as TrainAccessor).migratingPoints.clear()
                train.navigation.cancelNavigation()
                train.setGraph(null)
                train.setDerailed(true)
                train.status.highStress()
            }
        }
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
        val newScale = contraptionPosRot.scale
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

    fun updateShipShadow(
        entity: AbstractContraptionEntity,
        serverShip: ServerShip,
        posRot: ContraptionPosRot,
    ): ShipTransform {
        val transform = posRotToShipTransform(posRot, serverShip, entity.level as ServerLevel)
        serverShip.transformProvider = object : ServerShipTransformProvider {
            @NoOptimize
            override fun provideNextTransformAndVelocity(
                prevShipTransform: ShipTransform,
                shipTransform: ShipTransform
            ): ServerShipTransformProvider.NextTransformAndVelocityData? {
                if (entity is CarriageContraptionEntity && isTrainDerailed(entity)) {
                    return null
                }
                val prevPos = prevShipTransform.shipToWorld.transformPosition(transform.positionInShip, Vector3d())
                val velocityAtContraptionPos: Vector3dc = transform.positionInWorld.sub(prevPos, Vector3d()).mul(20.0)
                val rotDiff: Quaterniondc = transform.shipToWorldRotation.difference(prevShipTransform.shipToWorldRotation, Quaterniond()).normalize()
                val omega: Vector3dc = Vector3d(rotDiff.x() * 2.0, rotDiff.y() * 2.0, rotDiff.z() * 2.0).apply { if (rotDiff.w() > 0.0) mul(-1.0) }.mul(20.0)
                return ServerShipTransformProvider.NextTransformAndVelocityData(transform, velocityAtContraptionPos, omega)
            }
        }

        // If the ship is in the wrong dimension then teleport it
        if (entity.level.dimensionId != serverShip.chunkClaimDimension) {
            teleportShipToPosRot(posRot, serverShip, entity.level as ServerLevel)
        }

        // Make the ship static, so it won't be affected by physics
        serverShip.isStatic = true
        serverShip.enableKinematicVelocity = true
        // Don't let the ship teleport through dimensions on its own
        serverShip.settings.changeDimensionOnTouchPortals = false

        return transform
    }

    fun moveContraptionToTransform(entity: CarriageContraptionEntity, ship: Ship) {
        val shipTransform = ship.transform
        val rotState = CreateInteractiveContraptionRotationState(shipTransform.shipToWorldRotation)
        (entity as OrientedContraptionEntityDuck).`ci$setForcedRotation`(rotState)

        // Anchor at ship center of mass
        val shipCenter: Vector3ic = ship.getChunkClaimCenterPos(entity.level)
        val newPos: Vector3dc = shipTransform.shipToWorld.transformPosition(Vector3d(shipCenter).add(0.5, 0.5, 0.5))

        // Add (.5, 0, .5) to compensate for the anchorVec offset
        entity.setPos(newPos.x(), newPos.y() - 0.5, newPos.z())

        // Move the dimensional entity as well to fix the sus
        (entity.carriage.getDimensional(entity.level) as DimensionalCarriageEntityAccessor).setPositionAnchor(Vec3(newPos.x(), newPos.y() - 0.5, newPos.z()))

        // Update the bounding box too to handle ship rotation
        val box = entity.contraption.bounds
        if (box != null) {
            val boxInLocal: AABBdc = box.toJOML().translate(shipCenter.x() + 0.5, shipCenter.y() + 0.5, shipCenter.z() + 0.5)
            val boxInGlobal: AABBdc = boxInLocal.transform(shipTransform.shipToWorld, AABBd())
            entity.boundingBox = boxInGlobal.toMinecraft()
        }
    }

    data class ContraptionPosRot(val pos: Vector3dc, val rot: Quaterniondc, val scale: Double)

    fun getContraptionPosRot(entity: AbstractContraptionEntity): ContraptionPosRot {
        val rotationStateOriginal = AbstractContraptionEntity::class.java.cast(entity).rotationState
        val newRot = (rotationStateOriginal as ContraptionRotationStateDuck).`ci$getRotationQuaternion`(Quaterniond())
        val contraptionPos: Vector3dc = entity.anchorVec.toJOML().add(0.5, 0.5, 0.5)

        // Train on a train!!!
        val parentShip = entity.level.getShipManagingPos(entity.position())
        if (parentShip != null) {
            val newNewPos = parentShip.transform.shipToWorld.transformPosition(contraptionPos, Vector3d())
            val newNewRot = parentShip.transform.shipToWorldRotation.mul(newRot, Quaterniond())
            return ContraptionPosRot(newNewPos, newNewRot, parentShip.transform.shipToWorldScaling.x())
        }

        return ContraptionPosRot(contraptionPos, newRot, 1.0)
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
            return ContraptionPosRot(newNewPos, newNewRot, parentShip.renderTransform.shipToWorldScaling.x())
        }

        return ContraptionPosRot(contraptionPos, newRot, 1.0)
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
            return ContraptionPosRot(newNewPos, newNewRot, parentTransform.shipToWorldScaling.x())
        }

        return ContraptionPosRot(entity.anchorVec.toJOML().add(0.5, 0.5, 0.5), newRot, 1.0)
    }

    fun getShipForMovementContext(context: MovementContext): Ship? = getShipForContraption(context.contraption)

    internal fun getShipForContraption(contraption: Contraption): Ship? {
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
        @NoOptimize
        override fun createNewShipTransform(oldShipTransform: ShipTransform): ShipTransform = ShipTransformImpl(
            positionInWorld = newPos,
            positionInShip = newPosInShip,
            shipToWorldRotation = newRot,
            shipToWorldScaling = newScale?.let { Vector3d(it) } ?: oldShipTransform.shipToWorldScaling,
        )
    }
}
