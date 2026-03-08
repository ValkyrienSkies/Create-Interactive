package org.valkyrienskies.create_interactive.mixin.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;

public class MixinSBBContraptionManager {
    /**
     * Don't render blocks for contraptions with shadow ships
     */
//    @Inject(method = "renderContraptionLayerSBB", at = @At("HEAD"), cancellable = true, remap = false)
//    private void preRenderContraptionLayerSBB(final ContraptionRenderInfo renderInfo, final RenderType layer, final VertexConsumer consumer, final CallbackInfo ci) {
//        if (CreateInteractiveUtil.INSTANCE.doesContraptionHaveShipLoaded(renderInfo.contraption)) {
//            ci.cancel();
//        }
//    }
}
