package org.valkyrienskies.create_interactive.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinOrientedContraptionEntityLogic;
import org.valkyrienskies.create_interactive.mixinducks.OrientedContraptionEntityDuck;

@Mixin(OrientedContraptionEntity.class)
public class MixinOrientedContraptionEntity implements OrientedContraptionEntityDuck {
    @Unique
    private AbstractContraptionEntity.ContraptionRotationState ci$forcedRotation = null;

    @Override
    public void ci$setForcedRotation(final AbstractContraptionEntity.ContraptionRotationState forcedRotation) {
        ci$forcedRotation = forcedRotation;
    }

    @Inject(method = "getRotationState", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetRotationState(final CallbackInfoReturnable<AbstractContraptionEntity.ContraptionRotationState> cir) {
        if (ci$forcedRotation != null) {
            cir.setReturnValue(ci$forcedRotation);
        }
    }

    @Inject(method = "applyLocalTransforms", at = @At("HEAD"), cancellable = true)
    private void preApplyLocalTransforms(PoseStack matrixStack, float partialTicks, CallbackInfo ci) {
        MixinOrientedContraptionEntityLogic.INSTANCE.preApplyLocalTransforms$create_interactive(
            OrientedContraptionEntity.class.cast(this), matrixStack, ci
        );
    }
}
