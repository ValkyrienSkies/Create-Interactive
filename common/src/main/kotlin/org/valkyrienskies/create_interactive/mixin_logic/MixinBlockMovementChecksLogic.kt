package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.trains.track.ITrackBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.content.interact_me.InteractMeBlock

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

    internal fun postIsBlockAttachedTowardsFallback(
        state: BlockState,
        direction: Direction,
        cir: CallbackInfoReturnable<Boolean>
    ) {
        if (state.block is InteractMeBlock) {
            cir.returnValue = state.getValue(DirectionalBlock.FACING) == direction.opposite
        }
        /*val block = state.block
        when (block) {
            is InteractMeBlock -> {
                cir.returnValue = state.getValue(DirectionalBlock.FACING) == direction.opposite
            }
        }*/
    }
}