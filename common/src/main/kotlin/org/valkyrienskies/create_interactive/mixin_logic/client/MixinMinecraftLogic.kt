package org.valkyrienskies.create_interactive.mixin_logic.client

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient.postTickClient

internal object MixinMinecraftLogic {
    internal fun postTick(pause: Boolean, level: ClientLevel?, connection: ClientPacketListener?, ci: CallbackInfo?) {
        if (!pause && level != null && connection != null) {
            postTickClient()
        }
    }
}
