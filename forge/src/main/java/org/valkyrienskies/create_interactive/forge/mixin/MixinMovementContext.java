package org.valkyrienskies.create_interactive.forge.mixin;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixin.deployer.DeployerBlockEntityAccessor;

/**
 * This mixin is sus because I want to support 1.18.2 Create 0.5.1c and 0.5.1f
 */
@Pseudo
@Mixin(MovementContext.class)
public class MixinMovementContext {
    @Inject(method = "getFilterFromBE", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
    private void preGetPlayer(CallbackInfoReturnable<FilterItemStack> cir) {
        final BlockEntity blockEntity = CreateInteractiveUtil.INSTANCE.getBlockEntity(MovementContext.class.cast(this));
        if (blockEntity instanceof final DeployerBlockEntity deployerBlockEntity) {
            cir.setReturnValue(com.simibubi.create.content.logistics.filter.FilterItemStack.of(((DeployerBlockEntityAccessor) deployerBlockEntity).getFiltering().getFilter()));
        }
    }
}
