package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinBlockMovementChecksLogic;

@Mixin(BlockMovementChecks.class)
public class MixinBlockMovementChecks {
    /**
     * Allow track blocks to be relocated
     */
    @Inject(method = "isMovementAllowed", at = @At("HEAD"), cancellable = true)
    private static void preIsMovementAllowedFallback(
        BlockState state,
        Level world,
        BlockPos pos,
        CallbackInfoReturnable<Boolean> cir
    ) {
        MixinBlockMovementChecksLogic.INSTANCE.preIsMovementAllowed$create_interactive(state, cir);
    }
}
