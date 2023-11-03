package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Disable this entirely
 */
@Mixin(DeployerMovingInteraction.class)
public class MixinDeployerMovingInteraction {
    @Inject(method = "handlePlayerInteraction", at = @At("HEAD"), cancellable = true, remap = false)
    private void preHandlePlayerInteraction(final Player player, final InteractionHand activeHand, final BlockPos localPos, final AbstractContraptionEntity contraptionEntity, final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
