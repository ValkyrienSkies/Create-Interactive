package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.Create
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.ControlledContraptionEntity
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.trains.entity.CarriageContraption
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import com.simibubi.create.content.trains.entity.Train
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3dc
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient.addShipToContraptionRef
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.createShipForContraption
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionPosRot
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.linkShipToContraption
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.teleportShipToPosRot
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.unlinkShipToContraption
import org.valkyrienskies.create_interactive.mixin.ControlledContraptionEntityAccessor
import org.valkyrienskies.create_interactive.mixinducks.CarriageDuck
import org.valkyrienskies.create_interactive.mixinducks.TrainDuck
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.settings
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft
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
        return newShadowShipId
    }

    internal fun preReadAdditional(thisEntity: AbstractContraptionEntity, oldShadowShipId: ShipId?, compound: CompoundTag, spawnData: Boolean, ci: CallbackInfo): ShipId? {
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
        return if (compound.contains(SHADOW_SHIP_ID_NBT_KEY)) {
            compound.getLong(SHADOW_SHIP_ID_NBT_KEY)
        } else {
            setShadowShipId(thisEntity, oldShadowShipId, createShipForContraption((thisEntity.level as ServerLevel?)!!, thisEntity.contraption, BlockPos(thisEntity.position())))
        }
    }

    internal fun postTick(thisEntity: AbstractContraptionEntity, oldShadowShipId: ShipId?): ShipId? {
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

        return oldShadowShipId
    }

    internal fun writeAdditional(compound: CompoundTag, shadowShipId: ShipId?) {
        if (shadowShipId != null) {
            compound.putLong(SHADOW_SHIP_ID_NBT_KEY, shadowShipId)
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

        if (ship == null) {
            context.motion = actorPosition.subtract(previousPosition)
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

                // It seems that the actual vector for this doesn't matter, only the magnitude does
                motion.set(speed, 0.0, 0.0)
            }

            context.motion = motion.toMinecraft()
        }

        val contraptionEntity = context.contraption.entity

        if (!entity.level.isClientSide() && contraptionEntity is CarriageContraptionEntity && contraptionEntity.carriage != null) {
            val train: Train = contraptionEntity.carriage.train
            val actualSpeed = if (train.speedBeforeStall != null) train.speedBeforeStall else train.speed
            context.motion = context.motion.normalize()
                .scale(abs(actualSpeed))
        }

        var relativeMotion = context.motion
        relativeMotion = entity.reverseRotation(relativeMotion, 1f)
        context.relativeMotion = relativeMotion

        return (BlockPos(previousPosition) != gridPosition
            || (context.relativeMotion.length() > 0 || context.contraption is CarriageContraption)
            && context.firstMovement)
    }
}
