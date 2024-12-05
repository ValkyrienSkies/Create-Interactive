package org.valkyrienskies.create_interactive.mixin.sliding_door;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorMovementBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

@Mixin(SlidingDoorMovementBehaviour.class)
public class MixinSlidingDoorMovementBehaviour {
    /**
     * @author Triode
     * @reason Fix the doors
     */
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void preTick(final MovementContext context, final CallbackInfo ci) {
        if (context.contraption.entity instanceof AbstractContraptionEntityDuck duck && duck.ci$getShadowShipId() != null) {
            ci.cancel();
        }
    }
}
