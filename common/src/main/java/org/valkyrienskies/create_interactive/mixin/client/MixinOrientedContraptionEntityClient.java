package org.valkyrienskies.create_interactive.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinOrientedContraptionEntityClientLogic;

@Mixin(OrientedContraptionEntity.class)
public class MixinOrientedContraptionEntityClient {
    @Inject(method = "applyLocalTransforms", at = @At("HEAD"), cancellable = true)
    private void preApplyLocalTransforms(
        final PoseStack matrixStack,
        final float partialTicks,
        final CallbackInfo ci
    ) {
        MixinOrientedContraptionEntityClientLogic.INSTANCE.preApplyLocalTransforms$create_interactive(
            OrientedContraptionEntity.class.cast(this), matrixStack, ci
        );
    }
}
