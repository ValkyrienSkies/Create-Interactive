package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.contraptions.actors.psi.PIInstance;
import com.simibubi.create.content.contraptions.actors.psi.PSIActorVisual;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(PSIActorVisual.class)
public class MixinPSIActorInstance implements ActorInstanceDuck {
    @Shadow(remap = false)
    @Final
    private PIInstance instance;

    @Override
    public @NotNull List<AbstractInstance> ci$getInstances() {
        final PIInstanceAccessor accessor = (PIInstanceAccessor) instance;
        return ImmutableList.of(accessor.getMiddle(), accessor.getTop());
    }
}
