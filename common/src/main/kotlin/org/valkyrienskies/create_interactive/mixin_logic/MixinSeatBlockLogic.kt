package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionEntityForShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toBlockPos

internal object MixinSeatBlockLogic {
    internal fun preUpdateEntityAfterFallOn(reader: BlockGetter, entity: Entity, ci: CallbackInfo) {
        if (entity.vehicle is AbstractContraptionEntity) {
            ci.cancel()
        }
    }

    /**
     * If this seat is on a contraption ship, then mount entities to the contraption directly instead of the seat
     */
    internal fun preSitDown(level: Level, pos: BlockPos, entity: Entity, ci: CallbackInfo) {
        val ship = level.getShipManagingPos(pos) ?: return
        val contraptionEntity =
            getContraptionEntityForShip(ship.id, level.isClientSide) ?: return
        val shipCenter = ship.getChunkClaimCenterPos(level)
        val relativePos = pos.subtract(shipCenter.toBlockPos())
        val relativePosSeatIndex = contraptionEntity.contraption.seats.indexOf(relativePos)
        if (relativePosSeatIndex != -1) {
            // Check if the seat is occupied
            val seatOccupied = contraptionEntity.contraption.seatMapping.containsValue(relativePosSeatIndex)
            if (!seatOccupied) {
                contraptionEntity.addSittingPassenger(entity, relativePosSeatIndex)
            }
            // Cancel whether we sat down or not
            ci.cancel()
        }
    }
}
