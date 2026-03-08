package org.valkyrienskies.create_interactive.mixin.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinVisualManagerLogic;

/**
 * Disable rendering of block entities with actors in contraption shadow ships
 */
@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {
    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void preGetRenderer(E blockEntity, CallbackInfoReturnable<BlockEntityRenderer<E>> cir) {
        if(MixinVisualManagerLogic.INSTANCE.shouldRemoveBlockEntityInShip$create_interactive(blockEntity)) {
            cir.setReturnValue(null);
        }
    }
}
