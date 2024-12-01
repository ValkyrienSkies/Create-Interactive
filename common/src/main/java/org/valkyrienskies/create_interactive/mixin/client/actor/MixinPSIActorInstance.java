package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.jozufozu.flywheel.api.InstanceData;
import com.simibubi.create.content.contraptions.actors.psi.PIInstance;
import com.simibubi.create.content.contraptions.actors.psi.PSIActorInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(PSIActorInstance.class)
public class MixinPSIActorInstance implements ActorInstanceDuck {
    @Shadow(remap = false)
    @Final
    private PIInstance instance;

    @Override
    public @NotNull List<InstanceData> ci$getInstances() {
        final PIInstanceAccessor accessor = (PIInstanceAccessor) instance;
        return ImmutableList.of(accessor.getMiddle(), accessor.getTop());
    }
}
