package org.valkyrienskies.create_interactive.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
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
    @Shadow(remap = false)
    @Final
    private Map<Object, Outliner.OutlineEntry> outlines;

    @Inject(method = "renderOutlines", at = @At("HEAD"), cancellable = true)
    private void preRenderOutlines(
            PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 camera, float pt, CallbackInfo ci
    ) {
        MixinOutlinerLogic.INSTANCE.preRenderOutlines$create_interactive(outlines, ci);
    }
}
