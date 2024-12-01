package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.api.InstanceData;
import com.simibubi.create.content.contraptions.actors.flwdata.ActorData;
import com.simibubi.create.content.kinetics.drill.DrillActorInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(DrillActorInstance.class)
public class MixinDrillActorInstance implements ActorInstanceDuck {
    @Shadow(remap = false)
    ActorData drillHead;

    @Override
    public List<InstanceData> ci$getInstances() {
        return ImmutableList.of(drillHead);
    }
}
