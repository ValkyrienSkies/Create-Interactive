package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsScreen;
import com.simibubi.create.foundation.gui.AllIcons;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.create_interactive.CreateInteractiveIcons;

@Mixin(ValueSettingsScreen.class)
public class MixinValueSettingsScreen {

    // TODO: Is this mixin necessary?
    @Redirect(method = "renderWindow", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/gui/AllIcons;render(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
    private void redirectIconRender(final AllIcons instance, final GuiGraphics graphics, final int x, final int y) {
        if (instance instanceof CreateInteractiveIcons cIcon) {
            cIcon.render(graphics, x, y);
        } else {
            instance.render(graphics, x, y);
        }
    }
}
