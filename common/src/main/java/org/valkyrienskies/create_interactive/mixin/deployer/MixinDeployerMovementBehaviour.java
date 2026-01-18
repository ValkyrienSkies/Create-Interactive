package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "visitNewPosition", at = @At("HEAD"), cancellable = true, remap = false)
    private void preVisitNewPosition(MovementContext context, BlockPos pos, CallbackInfo ci) {
        MixinDeployerMovementBehaviourLogic.INSTANCE.preVisitNewPosition$create_interactive(context, ci);
    }

    /**
     * Require none of these because this doesn't work on create 0.5.1f
     */
    @Inject(method = "getFilter", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private void preGetFilter(final MovementContext context, final CallbackInfoReturnable<ItemStack> cir) {
        MixinDeployerMovementBehaviourLogic.INSTANCE.preGetFilter$create_interactive(context, cir);
    }

    @Inject(method = "getMode", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetMode(final MovementContext context, final CallbackInfoReturnable<Object> cir) {
        MixinDeployerMovementBehaviourLogic.INSTANCE.preGetMode$create_interactive(context, cir);
    }
}
