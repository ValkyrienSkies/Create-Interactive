package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.trains.track.ITrackBlock
import net.minecraft.world.level.block.state.BlockState
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

internal object MixinBlockMovementChecksLogic {
    /**
     * Allow track blocks to be relocated
     */
    internal fun preIsMovementAllowedFallback(
        state: BlockState,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        if (state.block == AllBlocks.TRACK || state.block is ITrackBlock) {
            cir.returnValue = true
        }
    }
}