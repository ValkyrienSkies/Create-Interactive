package org.valkyrienskies.create_interactive.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import com.simibubi.create.foundation.gui.AllIcons;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.valkyrienskies.create_interactive.CreateInteractiveIcons;

@Mixin(ValueBox.IconValueBox.class)
public class MixinIconValueBox {

    @Shadow(remap = false)
    AllIcons icon;

    @Inject(method = "renderContents", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllIcons;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void altRenderContents(PoseStack ms, MultiBufferSource buffer, CallbackInfo ci, float scale, int overrideColor) {
        if (icon instanceof CreateInteractiveIcons cIcon) {
            cIcon.render(ms, buffer, overrideColor != -1 ? overrideColor : 0xFFFFFF);
            ci.cancel();
        }
    }
}
