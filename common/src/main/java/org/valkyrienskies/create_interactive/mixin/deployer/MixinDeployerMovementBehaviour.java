package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.deployer.MixinDeployerMovementBehaviourLogic;

/**
 * Fix DeployerMovementBehaviour
 */
@Mixin(DeployerMovementBehaviour.class)
public class MixinDeployerMovementBehaviour {

    @Inject(method = "getPlayer", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetPlayer(final MovementContext context, final CallbackInfoReturnable<DeployerFakePlayer> cir) {
        MixinDeployerMovementBehaviourLogic.INSTANCE.preGetPlayer$create_interactive(context, cir);
    }

    @Inject(method = "getFilter", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetFilter(final MovementContext context, final CallbackInfoReturnable<ItemStack> cir) {
        MixinDeployerMovementBehaviourLogic.INSTANCE.preGetFilter$create_interactive(context, cir);
    }

    @Inject(method = "getMode", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetMode(final MovementContext context, final CallbackInfoReturnable<Object> cir) {
        MixinDeployerMovementBehaviourLogic.INSTANCE.preGetMode$create_interactive(context, cir);
    }
}
