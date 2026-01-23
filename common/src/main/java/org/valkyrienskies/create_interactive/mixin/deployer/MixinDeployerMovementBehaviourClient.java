package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.deployer.MixinDeployerMovementBehaviourLogic;

@Mixin(DeployerMovementBehaviour.class)
public class MixinDeployerMovementBehaviourClient {
    @Inject(
            method = "createVisual", at = @At("HEAD"), cancellable = true, remap = false
    )
    private void preCreateVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext, CallbackInfoReturnable<ActorVisual> cir) {
        MixinDeployerMovementBehaviourLogic.INSTANCE.preCreateVisual$create_interactive(movementContext.contraption, cir);
    }
}
