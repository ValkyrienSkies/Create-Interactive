package org.valkyrienskies.create_interactive.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinBlockEntityRenderDispatcherLogic;

/**
 * Disable rendering of block entities with actors in contraption shadow ships
 */
@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void preRender(final E blockEntity, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final CallbackInfo ci) {
        MixinBlockEntityRenderDispatcherLogic.INSTANCE.preRender$create_interactive(blockEntity, ci);
    }
}
