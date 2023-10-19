package org.valkyrienskies.create_interactive.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.dont_delete.CreateInteractiveEventsClient;
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider;

// Set priority to 1001 to run these after VS2 mixins
@Mixin(value = Minecraft.class, priority = 1001)
public abstract class MixinMinecraft implements IShipObjectWorldClientProvider {
    @Shadow
    private boolean pause;
    @Shadow
    public ClientLevel level;
    @Shadow
    public abstract ClientPacketListener getConnection();

    @Inject(
        method = "tick",
        at = @At("TAIL")
    )
    public void postTick(final CallbackInfo ci) {
        if (!pause && level != null && getConnection() != null) {
            CreateInteractiveEventsClient.INSTANCE.postTickClient();
        }
    }
}
