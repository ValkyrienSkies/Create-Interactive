package org.valkyrienskies.create_interactive.mixin.seat;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.seat.ContraptionPlayerPassengerRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

@Mixin(ContraptionPlayerPassengerRotation.class)
public class MixinContraptionPlayerPassengerRotation {
    @Inject(
            method = "frame",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;getRotationState()Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity$ContraptionRotationState;"),
            cancellable = true
    )
    private static void cancelPlayerRotation(CallbackInfo ci, @Local(name = "contraptionEntity") AbstractContraptionEntity contraptionEntity){
        if(((AbstractContraptionEntityDuck) contraptionEntity).ci$getShadowShipId() != null) {
            ci.cancel();
        }
    }
}
