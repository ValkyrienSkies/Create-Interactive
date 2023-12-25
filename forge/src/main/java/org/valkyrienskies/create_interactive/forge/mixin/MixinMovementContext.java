package org.valkyrienskies.create_interactive.forge.mixin;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

/**
 * This mixin is sus because I want to support 1.18.2 Create 0.5.1c and 0.5.1f
 */
@Pseudo
@Mixin(MovementContext.class)
public class MixinMovementContext {
    // TODO: Re-enable this later
//    @Inject(method = "getFilterFromBE", at = @At("RETURN"), cancellable = true, require = 0, remap = false)
//    private void preGetPlayer(CallbackInfoReturnable<FilterItemStack> cir) {
//        final BlockEntity blockEntity = CreateInteractiveUtil.INSTANCE.getBlockEntity(MovementContext.class.cast(this));
//        if (blockEntity instanceof final DeployerBlockEntity deployerBlockEntity) {
//            cir.setReturnValue(com.simibubi.create.content.logistics.filter.FilterItemStack.of(((DeployerBlockEntityAccessor) deployerBlockEntity).getFiltering().getFilter()));
//        }
//    }
}
