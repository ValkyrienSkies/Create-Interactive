package org.valkyrienskies.create_interactive.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinCarriageContraptionEntityRendererLogic;

@Mixin(CarriageContraptionEntityRenderer.class)
public class MixinCarriageContraptionEntityRenderer {
    @Inject(method = "translateBogey", at = @At("HEAD"), cancellable = true)
    private static void preTranslateBogey(
        final PoseStack ms,
        final CarriageBogey bogey,
        final int bogeySpacing,
        final float viewYRot,
        final float viewXRot,
        final float partialTicks,
        final CallbackInfo ci
    ) {
        MixinCarriageContraptionEntityRendererLogic.INSTANCE.preTranslateBogey$create_interactive(
            ms, bogey, bogeySpacing, partialTicks, ci
        );
    }
}
