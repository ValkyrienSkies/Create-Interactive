package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
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
}
