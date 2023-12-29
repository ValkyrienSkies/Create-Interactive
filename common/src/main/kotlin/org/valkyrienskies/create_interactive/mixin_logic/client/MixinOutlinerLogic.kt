package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.foundation.outliner.Outliner
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

internal object MixinOutlinerLogic {
    internal fun preRenderOutlines(
        outlines: MutableMap<Any?, Outliner.OutlineEntry?>,
        ci: CallbackInfo
    ) {
        if (outlines.size > 100000) {
            ci.cancel()
        }
    }
}
