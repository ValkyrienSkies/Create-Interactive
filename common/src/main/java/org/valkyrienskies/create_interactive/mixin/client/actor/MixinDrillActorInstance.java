package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.kinetics.drill.DrillActorVisual;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(DrillActorVisual.class)
public class MixinDrillActorInstance implements ActorInstanceDuck {
    @Shadow(remap = false)
    TransformedInstance drillHead;

    @Override
    public List<AbstractInstance> ci$getInstances() {
        return ImmutableList.of(drillHead);
    }
}
