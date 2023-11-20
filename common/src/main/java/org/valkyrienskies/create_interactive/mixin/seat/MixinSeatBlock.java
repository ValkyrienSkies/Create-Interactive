package org.valkyrienskies.create_interactive.mixin.seat;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
