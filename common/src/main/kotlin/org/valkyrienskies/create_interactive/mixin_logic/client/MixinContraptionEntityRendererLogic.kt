package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.content.contraptions.Contraption
import net.createmod.catnip.render.SuperByteBuffer
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.doesContraptionHaveShipLoaded

internal object MixinContraptionEntityRendererLogic {
    internal fun postBuildBuffer(
        c: Contraption,
        cir: CallbackInfoReturnable<SuperByteBuffer>
    ) {
        // Only disable block rendering if the contraption has a ship
        if (doesContraptionHaveShipLoaded(c)) {
            cir.returnValue = cir.returnValue.reset()
        }
    }

    internal fun preRenderBlockEntities(c: Contraption, ci: CallbackInfo) {
        // Only disable block entity rendering if the contraption has a ship
        if (doesContraptionHaveShipLoaded(c)) {
            ci.cancel()
        }
    }
}
