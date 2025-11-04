package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinContraptionRenderDispatcherLogic;

/**
 * Completely disable contraption block rendering
 */
@Mixin(ContraptionEntityRenderer.class)
public class MixinContraptionRenderDispatcher {
    @Inject(method = "buildStructureBuffer", at = @At("HEAD"), cancellable = true)
    private static SuperByteBuffer preBuildStructureBuffer(final VirtualRenderWorld renderWorld, final Contraption c, final RenderType layer, final CallbackInfoReturnable<SuperByteBuffer> cir) {
        MixinContraptionRenderDispatcherLogic.INSTANCE.preBuildStructureBuffer$create_interactive(renderWorld, c, layer, cir);
    }

    @Inject(method = "renderBlockEntities", at = @At("HEAD"), cancellable = true)
    private static void preRenderBlockEntities(final Level world, final VirtualRenderWorld renderWorld, final Contraption c, final ContraptionMatrices matrices, final MultiBufferSource buffer, final CallbackInfo ci) {
        MixinContraptionRenderDispatcherLogic.INSTANCE.preRenderBlockEntities$create_interactive(c, ci);
    }
}
