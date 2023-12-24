package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.Create
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.ContraptionHandler
import com.simibubi.create.content.contraptions.ControlledContraptionEntity
import com.simibubi.create.content.contraptions.OrientedContraptionEntity
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.elevator.ElevatorColumn
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption
import com.simibubi.create.content.decoration.slidingDoor.DoorControl
import com.simibubi.create.content.decoration.slidingDoor.DoorControlBehaviour
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlock
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity
import com.simibubi.create.content.trains.entity.CarriageContraption
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import com.simibubi.create.content.trains.entity.Train
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3dc
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient.addShipToContraptionRef
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.createShipForContraption
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionEntityForShip
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionPosRot
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.linkShipToContraption
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.teleportShipToPosRot
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.unlinkShipToContraption
import org.valkyrienskies.create_interactive.mixin.ControlledContraptionEntityAccessor
import org.valkyrienskies.create_interactive.mixin.MovementContextAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.create_interactive.mixinducks.CarriageDuck
import org.valkyrienskies.create_interactive.mixinducks.TrainDuck
import org.valkyrienskies.mod.common.entity.ShipMountedToData
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.settings
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.max

internal object MixinAbstractContraptionEntityLogic {
    private const val SHADOW_SHIP_ID_NBT_KEY = "ShadowShipId"

    /**
     * Returns the new ship id for thisEntity
     */
    internal fun setShadowShipId(thisEntity: AbstractContraptionEntity, oldShadowShipId: ShipId?, newShadowShipId: ShipId?): ShipId? {
        if (thisEntity is CarriageContraptionEntity) {
            val carriage = thisEntity.carriage
            if (carriage != null && !(carriage as CarriageDuck).`ci$doesCarriageEntityControlShip`(
                    thisEntity,
                    newShadowShipId
                )
            ) {
                // Do not set this if this entity doesn't control the ship
                return oldShadowShipId
            }
        }
        val prevShipId: ShipId? = oldShadowShipId
        if (newShadowShipId != null) {
            linkShipToContraption(newShadowShipId, thisEntity)
            val serverShip: ServerShip? =
                (thisEntity.level as ServerLevel).shipObjectWorld.allShips.getById(newShadowShipId)
            if (serverShip == null) {
                // How???!
                println("Absolute giga-sus!!!")
                return newShadowShipId
            }
            // Derailed trains can move freely
            if (thisEntity is CarriageContraptionEntity && CreateInteractiveUtil.isTrainDerailed(thisEntity)) {
                serverShip.isStatic = false
                disableCollisions(thisEntity, prevShipId, newShadowShipId, disabled = false)
                // TODO: Move the contraption to follow the train
                return newShadowShipId
            }
            val contraptionPosRot = getContraptionPosRot(thisEntity)
            teleportShipToPosRot(
                contraptionPosRot, serverShip,
                (thisEntity.level as ServerLevel?)!!
            )
            // Make the ship static, so it won't be affected by physics
            serverShip.isStatic = true
            // Don't let the ship teleport through dimensions on its own
            serverShip.settings.changeDimensionOnTouchPortals = false
        } else if (prevShipId != null) {
            unlinkShipToContraption(prevShipId, thisEntity)
        }

        disableCollisions(thisEntity, prevShipId, newShadowShipId)

        return newShadowShipId
    }

    internal fun preReadAdditional(thisEntity: AbstractContraptionEntity, oldShadowShipId: ShipId?, compound: CompoundTag, spawnData: Boolean): ShipId? {
        if (thisEntity.level.isClientSide) {
            return if (spawnData && compound.contains(SHADOW_SHIP_ID_NBT_KEY)) {
                val shadowShipId = compound.getLong(SHADOW_SHIP_ID_NBT_KEY)
                addShipToContraptionRef(shadowShipId, thisEntity)
                shadowShipId
            } else {
                null
            }
        }
        if (thisEntity.contraption == null) {
            return null
        }
        check(oldShadowShipId == null) { "Ship already exists" }
        val shipId = if (compound.contains(SHADOW_SHIP_ID_NBT_KEY)) {
            compound.getLong(SHADOW_SHIP_ID_NBT_KEY)
        } else {
            setShadowShipId(thisEntity, oldShadowShipId, createShipForContraption((thisEntity.level as ServerLevel?)!!, thisEntity.contraption, BlockPos(thisEntity.position())))
        }
        disableCollisions(thisEntity, null, shipId)
        return shipId
    }

    internal fun postTick(thisEntity: AbstractContraptionEntity, oldShadowShipId: ShipId?, extraData: ExtraData): ShipId? {
        // TODO: Its sus af that we have to keep linking the ship, but just do it!
        if (oldShadowShipId != null) {
            if (thisEntity is CarriageContraptionEntity) {
                if (thisEntity.carriage != null && (thisEntity.carriage as CarriageDuck).`ci$doesCarriageEntityControlShip`(
                        thisEntity,
                        oldShadowShipId
                    )
                ) {
                    linkShipToContraption(oldShadowShipId, thisEntity)
                } else {
                    unlinkShipToContraption(oldShadowShipId, thisEntity)
                    return null
                }
            } else {
                linkShipToContraption(oldShadowShipId, thisEntity)
            }
        }
        // Do this in MixinMinecraftServer instead
        // updateShipShadow(thisEntity)

        // Disassemble contraptions with no blocks
        if (!thisEntity.level.isClientSide && thisEntity.contraption.blocks.isEmpty()) {
            if (thisEntity is CarriageContraptionEntity) {
                val train = Create.RAILWAYS.sided(thisEntity.level).trains[thisEntity.trainId]
                (train as TrainDuck?)?.`ci$splitOrDisassemble`()
            } else {
                println("Trying to disassemble contraption $thisEntity")
                thisEntity.disassemble()
            }
        }

        if (!thisEntity.level.isClientSide && oldShadowShipId != null) {
            val serverShip: ServerShip? =
                (thisEntity.level as ServerLevel).shipObjectWorld.allShips.getById(oldShadowShipId)
            if (serverShip != null) {
                val prevControl: DoorControl? = extraData.forcedDoorControls
                extraData.forcedDoorControls = getCurrentDoorControl(thisEntity)
                // Skip when ci$forcedDoorFinishedFirstTick is false
                if (extraData.forcedDoorFinishedFirstTick && prevControl != extraData.forcedDoorControls) {
                    val shouldOpen: Boolean
                    val toUse: DoorControl?
                    if (extraData.forcedDoorControls == null) {
                        // Close all doors matching the prev direction
                        shouldOpen = false
                        toUse = prevControl
                    } else {
                        // Open all doors matching the direction
                        shouldOpen = true
                        toUse = extraData.forcedDoorControls
                    }
                    serverShip.activeChunksSet.forEach { chunkX: Int, chunkZ: Int ->
                        val levelChunk: LevelChunk = thisEntity.level.getChunk(chunkX, chunkZ)
                        for ((key, value) in levelChunk.blockEntities) {
                            if (value !is SlidingDoorBlockEntity) continue
                            val blockState: BlockState = value.getBlockState()
                            val block = blockState.block
                            if (block !is SlidingDoorBlock) continue
                            if (toUse != DoorControl.NONE) {
                                // TODO: Check if door direction matches door control
                                block.setOpen(null, thisEntity.level, blockState, key, shouldOpen)
                            }
                        }
                    }
                }
                extraData.forcedDoorFinishedFirstTick = true
            }
        }

        return oldShadowShipId
    }

    private fun getCurrentDoorControl(entity: AbstractContraptionEntity): DoorControl? {
        val contraption = entity.contraption
        val motion: Vector3dc =
            Vector3d(entity.position().x - entity.xo, entity.position().y - entity.yo, entity.position().z - entity.zo)
        // Tick sliding doors
        val canOpen = (motion.length() < 1 / 128f && !contraption.entity.isStalled
            || contraption is ElevatorContraption && contraption.arrived)
        if (!canOpen) return null
        var doorControlBehaviour: DoorControlBehaviour? = null
        if (contraption is ElevatorContraption) doorControlBehaviour = getElevatorDoorControl(entity, contraption)
        if (entity is CarriageContraptionEntity) doorControlBehaviour =
            getTrainStationDoorControl(entity)
        return doorControlBehaviour?.mode
    }

    private fun getElevatorDoorControl(entity: AbstractContraptionEntity, ec: ElevatorContraption): DoorControlBehaviour? {
        val level = entity.level
        val currentTargetY = ec.getCurrentTargetY(level) ?: return null
        val columnCoords = ec.globalColumn ?: return null
        val elevatorColumn = ElevatorColumn.get(level, columnCoords)
            ?: return null
        return BlockEntityBehaviour.get(level, elevatorColumn.contactAt(currentTargetY), DoorControlBehaviour.TYPE)
    }

    private fun getTrainStationDoorControl(cce: CarriageContraptionEntity): DoorControlBehaviour? {
        val carriage = cce.carriage
        if (carriage?.train == null) return null
        val currentStation = carriage.train.getCurrentStation() ?: return null
        val stationPos = currentStation.getBlockEntityPos()
        val stationDim = currentStation.getBlockEntityDimension()
        val server = cce.level.server ?: return null
        val stationLevel = server.getLevel(stationDim)
        return if (stationLevel == null || !stationLevel.isLoaded(stationPos)) null else BlockEntityBehaviour.get(
            stationLevel,
            stationPos,
            DoorControlBehaviour.TYPE
        )
    }

    internal fun writeAdditional(compound: CompoundTag, shadowShipId: ShipId?) {
        if (shadowShipId != null) {
            compound.putLong(SHADOW_SHIP_ID_NBT_KEY, shadowShipId)
        }
    }

    internal fun preDisassemble(entity: AbstractContraptionEntity, level: Level, shadowShipId: ShipId?) {
        if (shadowShipId == null) return
        if (!entity.isAlive) return
        if (entity.contraption == null) return
        val contraptionsInLevel = ContraptionHandler.loadedContraptions.get(level).values
        for (contraptionEntityRef: WeakReference<AbstractContraptionEntity> in contraptionsInLevel) {
            val contraptionEntity = contraptionEntityRef.get() ?: continue
            if (level.getShipManagingPos(contraptionEntity.anchorVec)?.id == shadowShipId) {
                val vehicle = contraptionEntity.vehicle
                if (vehicle is AbstractContraptionEntity) {
                    // This is a stabilized bearing contraption, disassemble the vehicle instead
                    vehicle.disassemble()
                } else {
                    contraptionEntity.disassemble()
                }
            }
        }
    }

    internal fun postDisassemble(level: Level, shadowShipId: ShipId?) {
        if (shadowShipId != null && level is ServerLevel) {
            val serverShip: ServerShip? = level.shipObjectWorld.allShips.getById(shadowShipId)
            if (serverShip != null) {
                level.shipObjectWorld.deleteShip(serverShip)
            }
        }
    }

    internal fun overwriteShouldActorTrigger(entity: AbstractContraptionEntity, context: MovementContext, actorPosition: Vec3, gridPosition: BlockPos): Boolean {
        val previousPosition = context.position ?: return false

        val ship = context.world.getShipManagingPos(actorPosition)

        // This accessor is used because progaurd breaks when using some MovementContext fields
        val contextAccessor = context as MovementContextAccessor

        if (ship == null) {
            contextAccessor.motion = actorPosition.subtract(previousPosition)
        } else {
            val prevPos: Vector3dc = ship.prevTickTransform.shipToWorld.transformPosition(previousPosition.toJOML())
            val curPos: Vector3dc = ship.transform.shipToWorld.transformPosition(actorPosition.toJOML())
            val motion: Vector3d = curPos.sub(prevPos, Vector3d())

            if (!entity.level.isClientSide() && entity is ControlledContraptionEntity) {
                // Angle delta, in degrees
                val angleDelta = (entity as ControlledContraptionEntityAccessor).angleDelta

                // Make 360 degrees per second equivalent to 8 blocks per second speed
                val angleSpeed = abs(angleDelta) * 8.0 / 360.0
                val motionSpeed = motion.length()

                val speed = max(angleSpeed, motionSpeed)

                if (motion.lengthSquared() > 1e-8) {
                    motion.normalize().mul(speed)
                } else {
                    // I just hope this works!
                    motion.set(speed, 0.0, 0.0)
                }
            }

            contextAccessor.motion = motion.toMinecraft()
        }

        val contraptionEntity = contextAccessor.contraption.entity

        if (!entity.level.isClientSide() && contraptionEntity is CarriageContraptionEntity && contraptionEntity.carriage != null) {
            val train: Train = contraptionEntity.carriage.train
            val actualSpeed = if (train.speedBeforeStall != null) train.speedBeforeStall else train.speed
            contextAccessor.motion = context.motion.normalize()
                .scale(abs(actualSpeed))
        }

        var relativeMotion = contextAccessor.motion
        relativeMotion = entity.reverseRotation(relativeMotion, 1f)
        contextAccessor.relativeMotion = relativeMotion

        return (BlockPos(previousPosition) != gridPosition
            || (contextAccessor.relativeMotion.length() > 0 || contextAccessor.contraption is CarriageContraption)
            && contextAccessor.firstMovement)
    }

    fun disableCollisions(thisEntity: AbstractContraptionEntity, prevShipId: ShipId?, newShadowShipId: ShipId?, disabled: Boolean = true) {
        // Disable collision between ships and sub-contraptions
        if (!thisEntity.level.isClientSide) {
            if (prevShipId != newShadowShipId) {
                // Disable collision to base ship pls
                var contraptionEntity: AbstractContraptionEntity? = thisEntity
                var parentShip: Ship? = thisEntity.level.getShipManagingPos(thisEntity.contraption.anchor)
                while (parentShip != null && contraptionEntity != null) {
                    contraptionEntity = getContraptionEntityForShip(parentShip.id, false)
                    if (contraptionEntity == null) break
                    parentShip = thisEntity.level.getShipManagingPos(contraptionEntity.contraption.anchor)
                }
                if (parentShip != null) {
                    // Disable collisions
                    if (newShadowShipId != null) {
                        if (disabled) {
                            ((thisEntity.level as ServerLevel).shipObjectWorld).disableCollisionBetweenBodies(
                                newShadowShipId,
                                parentShip.id,
                            )
                        } else {
                            ((thisEntity.level as ServerLevel).shipObjectWorld).enableCollisionBetweenBodies(
                                newShadowShipId,
                                parentShip.id,
                            )
                        }
                    }
                    if (prevShipId != null) {
                        ((thisEntity.level as ServerLevel).shipObjectWorld).enableCollisionBetweenBodies(
                            prevShipId,
                            parentShip.id,
                        )
                    }
                }
            }
        }
    }

    private fun AbstractContraptionEntity.getPassengerPosInShip(ship: Ship, passenger: Entity): Vector3dc? {
        val seatPos = contraption.getSeatOf(passenger.uuid) ?: return null
        return Vector3d(ship.getChunkClaimCenterPos(passenger.level)).add(seatPos.x + 0.5, seatPos.y + passenger.myRidingOffset, seatPos.z + 0.5)
    }

    internal fun provideShipMountedToData(
        contraptionEntity: AbstractContraptionEntity,
        passenger: Entity,
    ): ShipMountedToData? {
        // Don't do this for Create's sub-contraptions
        if (passenger is OrientedContraptionEntity) {
            return null
        }

        val shadowShipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return null
        val ship = contraptionEntity.level.shipObjectWorld.loadedShips.getById(shadowShipId) ?: return null

        val passengerPosInLocal = contraptionEntity.getPassengerPosInShip(ship, passenger) ?: return null

        return ShipMountedToData(
            shipMountedTo = ship,
            mountPosInShip = passengerPosInLocal,
        )
    }

    internal fun prePositionRider(contraptionEntity: AbstractContraptionEntity, passenger: Entity, callback: Entity.MoveFunction, ci: CallbackInfo) {
        if (passenger is AbstractContraptionEntity) {
            return
        }

        val shadowShipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`()
        val ship = contraptionEntity.level.shipObjectWorld.loadedShips.getById(shadowShipId) ?: return
        val passengerPosInLocal = contraptionEntity.getPassengerPosInShip(ship, passenger) ?: return

        callback.accept(
            passenger, passengerPosInLocal.x(), passengerPosInLocal.y(), passengerPosInLocal.z()
        )

        ci.cancel()
    }

    internal data class ExtraData(
        var forcedDoorControls: DoorControl? = null,
        var forcedDoorFinishedFirstTick: Boolean = false,
    )
}
