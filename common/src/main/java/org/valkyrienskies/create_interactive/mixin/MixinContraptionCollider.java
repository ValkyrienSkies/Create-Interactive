package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContraptionCollider.class)
public class MixinContraptionCollider {
    /**
     * Disable contraption collision entirely! We will get it from VS2 instead!
     */
    @Inject(method = "collideEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preCollideEntities(AbstractContraptionEntity contraptionEntity, CallbackInfo ci) {
        ci.cancel();
    }
}
