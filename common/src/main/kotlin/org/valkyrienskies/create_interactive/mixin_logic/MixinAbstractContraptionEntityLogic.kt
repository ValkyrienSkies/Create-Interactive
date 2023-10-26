package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.Create
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient.addShipToContraptionRef
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.createShipForContraption
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionPosRot
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.linkShipToContraption
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.teleportShipToPosRot
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.unlinkShipToContraption
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.updateShipShadow
import org.valkyrienskies.create_interactive.mixinducks.CarriageDuck
import org.valkyrienskies.create_interactive.mixinducks.TrainDuck
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.settings

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
            }
        }
        updateShipShadow(thisEntity)

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
}
