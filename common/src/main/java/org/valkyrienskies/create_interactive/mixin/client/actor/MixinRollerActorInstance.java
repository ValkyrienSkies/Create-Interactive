package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.api.InstanceData;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.content.contraptions.actors.roller.RollerActorInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(RollerActorInstance.class)
public class MixinRollerActorInstance implements ActorInstanceDuck {
    @Shadow
    ModelData frame;

    @Override
    public @NotNull List<InstanceData> ci$getInstances() {
        return ImmutableList.of(frame);
    }
}
