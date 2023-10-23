package org.valkyrienskies.create_interactive.mixin_logic.client

import com.jozufozu.flywheel.core.model.WorldModelBuilder
import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.foundation.render.SuperByteBuffer
import net.minecraft.client.renderer.RenderType
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
            val data = WorldModelBuilder(layer).withRenderWorld(renderWorld)
                .withBlocks(emptyList())
                .build()
            val sbb = SuperByteBuffer(data)
            data.release()
            cir.setReturnValue(sbb)
        }
    }
}
