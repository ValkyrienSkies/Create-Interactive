package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.render.ClientContraption
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.RenderType
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.doesContraptionHaveShipLoaded

internal object MixinContraptionRenderDispatcherLogic {
    internal fun preBuildStructureBuffer(
        renderWorld: VirtualRenderWorld,
        c: Contraption,
        layer: RenderType,
        cir: CallbackInfoReturnable<SuperByteBuffer>
    ) {
        // Only disable block rendering if the contraption has a ship
        if (doesContraptionHaveShipLoaded(c)) {
            val sbb = ClientContraption.getBuffer(c, renderWorld, layer)
            cir.setReturnValue(sbb)
        }
    }

    internal fun preRenderBlockEntities(c: Contraption, ci: CallbackInfo) {
        // Only disable block entity rendering if the contraption has a ship
        if (doesContraptionHaveShipLoaded(c)) {
            ci.cancel()
        }
    }
}
