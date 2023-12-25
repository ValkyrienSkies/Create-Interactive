package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinControlsBlockLogic;

@Mixin(ControlsBlock.class)
public class MixinControlsBlock {
    /**
     * Fix control blocks being closed when placed on train contraptions
     */
    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void postGetStateForPlacement(
        final BlockPlaceContext pContext,
        final CallbackInfoReturnable<BlockState> cir
    ) {
        MixinControlsBlockLogic.INSTANCE.postGetStateForPlacement$create_interactive(pContext, cir);
    }

    /**
     * Fix control blocks being closed when updated on train contraptions
     */
    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void postUpdateShape(
        final BlockState pState,
        final Direction pDirection,
        final BlockState pNeighborState,
        final LevelAccessor pLevel,
        final BlockPos pCurrentPos,
        final BlockPos pNeighborPos,
        final CallbackInfoReturnable<BlockState> cir
    ) {
        MixinControlsBlockLogic.INSTANCE.postUpdateShape$create_interactive(pLevel, pCurrentPos, cir);
    }
}
