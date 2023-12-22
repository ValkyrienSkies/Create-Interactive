package org.valkyrienskies.create_interactive.mixin.seat;

import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinSeatBlockLogic;

@Mixin(SeatBlock.class)
public class MixinSeatBlock {
    /**
     * Don't mount entities to seat blocks if they're already riding a contraption
     */
    @Inject(method = "updateEntityAfterFallOn", at = @At("HEAD"), cancellable = true)
    private void preUpdateEntityAfterFallOn(final BlockGetter reader, final Entity entity, final CallbackInfo ci) {
        MixinSeatBlockLogic.INSTANCE.preUpdateEntityAfterFallOn$create_interactive(reader, entity, ci);
    }

    /**
     * If this seat is on a contraption ship, then mount entities to the contraption directly instead of the seat
     */
    @Inject(method = "sitDown", at = @At("HEAD"), cancellable = true)
    private static void preSitDown(final Level level, final BlockPos pos, final Entity entity, final CallbackInfo ci) {
        MixinSeatBlockLogic.INSTANCE.preSitDown$create_interactive(level, pos, entity, ci);
    }
}
