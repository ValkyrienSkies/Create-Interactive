package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinContraptionEntityRendererLogic;

/**
 * Completely disable contraption block rendering
 */
@Mixin(ContraptionEntityRenderer.class)
public class MixinContraptionEntityRenderer {
    @Inject(method = "buildStructureBuffer", at = @At("RETURN"), cancellable = true)
    private static void postBuildBuffer(Contraption c, VirtualRenderWorld renderWorld, RenderType layer, CallbackInfoReturnable<SuperByteBuffer> cir) {
        MixinContraptionEntityRendererLogic.INSTANCE.postBuildBuffer$create_interactive(c, cir);
    }

//    @Inject(method = "render(Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), cancellable = true)
//    private static void preRenderBlockEntities(final Level world, final VirtualRenderWorld renderWorld, final Contraption c, final ContraptionMatrices matrices, final MultiBufferSource buffer, final CallbackInfo ci) {
//        MixinContraptionRenderDispatcherLogic.INSTANCE.preRenderBlockEntities$create_interactive(c, ci);
//    }
}
