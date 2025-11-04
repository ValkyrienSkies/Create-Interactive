package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.content.kinetics.deployer.DeployerActorVisual;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(DeployerActorVisual.class)
public class MixinDeployerActorInstance implements ActorInstanceDuck {
    @Shadow(remap = false)
    TransformedInstance pole;
    @Shadow(remap = false)
    TransformedInstance hand;
    @Shadow(remap = false)
    RotatingInstance shaft;

    @Override
    public @NotNull List<AbstractInstance> ci$getInstances() {
        return ImmutableList.of(pole, hand, shaft);
    }
}
