package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.api.InstanceData;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(StabilizedBearingInstance.class)
public class MixinStabilizedBearingInstance implements ActorInstanceDuck {
    @Shadow
    @Final
    OrientedData topInstance;
    @Shadow
    @Final
    RotatingData shaft;
    @Override
    public @NotNull List<InstanceData> ci$getInstances() {
        return ImmutableList.of(topInstance, shaft);
    }
}
