package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(StabilizedBearingVisual.class)
public class MixinStabilizedBearingInstance implements ActorInstanceDuck {
    @Shadow(remap = false)
    @Final
    OrientedInstance topInstance;
    @Shadow(remap = false)
    @Final
    RotatingInstance shaft;
    @Override
    public @NotNull List<AbstractInstance> ci$getInstances() {
        return ImmutableList.of(topInstance, shaft);
    }
}
