package org.valkyrienskies.create_interactive.mixin.client;

import com.jozufozu.flywheel.core.model.ShadeSeparatedBufferedData;
import com.jozufozu.flywheel.core.model.WorldModelBuilder;
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.render.ContraptionRenderDispatcher;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;

import java.util.Collections;

/**
 * Completely disable contraption block rendering
 */
@Mixin(ContraptionRenderDispatcher.class)
public class MixinContraptionRenderDispatcher {
    @Inject(method = "buildStructureBuffer", at = @At("HEAD"), cancellable = true)
    private static void preBuildStructureBuffer(final VirtualRenderWorld renderWorld, final Contraption c, final RenderType layer, final CallbackInfoReturnable<SuperByteBuffer> cir) {
        // Only disable block rendering if the contraption has a ship
        if (CreateInteractiveUtil.INSTANCE.doesContraptionHaveShipLoaded(c)) {
            final ShadeSeparatedBufferedData data = new WorldModelBuilder(layer).withRenderWorld(renderWorld)
                    .withBlocks(Collections.emptyList())
                    .build();
            final SuperByteBuffer sbb = new SuperByteBuffer(data);
            data.release();
            cir.setReturnValue(sbb);
        }
    }
}
