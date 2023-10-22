package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.api.InstanceData;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import com.simibubi.create.content.kinetics.deployer.DeployerActorInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(DeployerActorInstance.class)
public class MixinDeployerActorInstance implements ActorInstanceDuck {
    @Shadow
    ModelData pole;
    @Shadow
    ModelData hand;
    @Shadow
    RotatingData shaft;

    @Override
    public @NotNull List<InstanceData> ci$getInstances() {
        return ImmutableList.of(pole, hand, shaft);
    }
}
