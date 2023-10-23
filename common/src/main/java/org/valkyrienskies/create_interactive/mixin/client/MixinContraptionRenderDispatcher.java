package org.valkyrienskies.create_interactive.mixin.client;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinContraptionRenderDispatcherLogic;

/**
 * Completely disable contraption block rendering
 */
@Mixin(ContraptionRenderDispatcher.class)
public class MixinContraptionRenderDispatcher {
    @Inject(method = "buildStructureBuffer", at = @At("HEAD"), cancellable = true)
    private static void preBuildStructureBuffer(final VirtualRenderWorld renderWorld, final Contraption c, final RenderType layer, final CallbackInfoReturnable<SuperByteBuffer> cir) {
        MixinContraptionRenderDispatcherLogic.INSTANCE.preBuildStructureBuffer$create_interactive(renderWorld, c, layer, cir);
    }
}
