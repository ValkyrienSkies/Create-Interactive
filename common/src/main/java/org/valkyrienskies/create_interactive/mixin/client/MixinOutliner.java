package org.valkyrienskies.create_interactive.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.outliner.Outliner;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinOutlinerLogic;

import java.util.Map;

@Mixin(Outliner.class)
public class MixinOutliner {
    @Shadow
    @Final
    private Map<Object, Outliner.OutlineEntry> outlines;

    @Inject(method = "renderOutlines", at = @At("HEAD"), cancellable = true)
    private void preRenderOutlines(
        final PoseStack ms,
        final SuperRenderTypeBuffer buffer,
        final Vec3 camera,
        final float pt,
        final CallbackInfo ci
    ) {
        MixinOutlinerLogic.INSTANCE.preRenderOutlines$create_interactive(outlines, ci);
    }
}
