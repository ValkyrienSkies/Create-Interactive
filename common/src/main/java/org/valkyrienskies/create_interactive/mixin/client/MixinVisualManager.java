package org.valkyrienskies.create_interactive.mixin.client;

import dev.engine_room.flywheel.impl.visualization.storage.BlockEntityStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinVisualManagerLogic;

@Mixin(BlockEntityStorage.class)
public class MixinVisualManager {
    @Inject(method = "willAccept(Lnet/minecraft/world/level/block/entity/BlockEntity;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void preWillAccept(BlockEntity blockEntity, CallbackInfoReturnable<Boolean> cir) {
        MixinVisualManagerLogic.INSTANCE.preWillAccept$create_interactive(blockEntity, cir);
    }
}
