package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

internal object MixinFunnelMovementBehaviourLogic {
    internal fun preExtract(context: MovementContext, ci: CallbackInfo) {
        if (context.world.isClientSide) {
            ci.cancel()
        }
    }
}
