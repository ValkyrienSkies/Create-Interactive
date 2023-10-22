package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.api.InstanceData;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterActorInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(HarvesterActorInstance.class)
public class MixinHarvesterActorInstance implements ActorInstanceDuck {
    @Shadow
    protected ModelData harvester;

    @Override
    public @NotNull List<InstanceData> ci$getInstances() {
        return ImmutableList.of(harvester);
    }
}
