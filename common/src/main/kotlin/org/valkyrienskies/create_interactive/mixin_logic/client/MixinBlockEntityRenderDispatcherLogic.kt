package org.valkyrienskies.create_interactive.mixin_logic.client

import net.minecraft.world.level.block.entity.BlockEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

internal object MixinBlockEntityRenderDispatcherLogic {
    internal fun <E : BlockEntity> preRender(
        blockEntity: E,
        ci: CallbackInfo
    ) {
        // Don't render bogeys or actors
        if (MixinInstanceManagerLogic.shouldRemoveBlockEntityInShip(blockEntity) ) {
            // Cancel the rendering
            ci.cancel()
        }
    }
}
