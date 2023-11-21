package org.valkyrienskies.create_interactive.mixin.seat;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(SeatBlock.class)
public class MixinSeatBlock {
    /**
     * Don't mount entities to seat blocks if they're already riding a contraption
     */
    @Inject(method = "updateEntityAfterFallOn", at = @At("HEAD"), cancellable = true)
    private void preUpdateEntityAfterFallOn(final BlockGetter reader, final Entity entity, final CallbackInfo ci) {
        if (entity.getVehicle() instanceof AbstractContraptionEntity) {
            ci.cancel();
        }
    }

    /**
     * If this seat is on a contraption ship, then mount entities to the contraption directly instead of the seat
     */
    @Inject(method = "sitDown", at = @At("HEAD"), cancellable = true)
    private static void preSitDown(final Level level, final BlockPos pos, final Entity entity, final CallbackInfo ci) {
        final Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) {
            return;
        }
        final AbstractContraptionEntity contraptionEntity = CreateInteractiveUtil.INSTANCE.getContraptionEntityForShip(ship.getId(), level.isClientSide);
        if (contraptionEntity == null) {
            return;
        }
        final Vector3ic shipCenter = CreateInteractiveUtil.INSTANCE.getChunkClaimCenterPos(ship, level);
        final BlockPos relativePos = pos.subtract(VectorConversionsMCKt.toBlockPos(shipCenter));
        final int relativePosSeatIndex = contraptionEntity.getContraption().getSeats().indexOf(relativePos);
        if (relativePosSeatIndex != -1) {
            // Check if the seat is occupied
            final boolean seatOccupied = contraptionEntity.getContraption().getSeatMapping().containsValue(relativePosSeatIndex);
            if (!seatOccupied) {
                contraptionEntity.addSittingPassenger(entity, relativePosSeatIndex);
            }
            // Cancel whether we sat down or not
            ci.cancel();
        }
    }
}
