package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;

@Mixin(ContraptionCollider.class)
public class MixinContraptionCollider {
    /**
     * Disable contraption collision entirely! We will get it from VS2 instead!
     */
    @Inject(method = "collideEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preCollideEntities(final AbstractContraptionEntity contraptionEntity, final CallbackInfo ci) {
        // Only disable collision if the contraption has a ship
        if (CreateInteractiveUtil.INSTANCE.doesContraptionHaveShipLoaded(contraptionEntity.getContraption())) {
            ci.cancel();
        }
    }
}
