package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MountedStorageManager.class)
public class MixinMountedStorageManager {
    @Inject(method = "handlePlayerStorageInteraction", at = @At("HEAD"), cancellable = true)
    private void preHandlePlayerStorageInteraction(final Contraption contraption, final Player player, final BlockPos localPos, final CallbackInfoReturnable<Boolean> cir) {
        // Disable this entirely
        cir.setReturnValue(false);
    }
}
