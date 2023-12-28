package org.valkyrienskies.create_interactive.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.create_interactive.CreateInteractiveIcons;

@Mixin(ValueSettingsScreen.class)
public class MixinValueSettingsScreen {

    @Redirect(method = "renderWindow", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllIcons;render(Lcom/mojang/blaze3d/vertex/PoseStack;II)V"))
    private void redirectIconRender(AllIcons instance, PoseStack matrixStack, int x, int y) {
        if (instance instanceof CreateInteractiveIcons cIcon) {
            cIcon.render(matrixStack, x, y);
        } else {
            instance.render(matrixStack, x, y);
        }
    }
}
