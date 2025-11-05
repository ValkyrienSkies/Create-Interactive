package org.valkyrienskies.create_interactive.mixin.client;

import dev.engine_room.flywheel.api.visual.BlockEntityVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinInstanceManagerLogic;

@Mixin(BlockEntityStorage.class)
public class MixinInstanceManager {
    @Inject(method = "createRaw(Ldev/engine_room/flywheel/api/visualization/VisualizationContext;Lnet/minecraft/world/level/block/entity/BlockEntity;F)Ldev/engine_room/flywheel/api/visual/BlockEntityVisual;", at = @At("HEAD"), cancellable = true, remap = false)
    private <T extends BlockEntity> void preCreateInternal(VisualizationContext visualizationContext, BlockEntity obj, float partialTick, CallbackInfoReturnable<BlockEntityVisual<T>> cir) {
        MixinInstanceManagerLogic.INSTANCE.preCreateInternal$create_interactive(obj, cir);
    }
}
