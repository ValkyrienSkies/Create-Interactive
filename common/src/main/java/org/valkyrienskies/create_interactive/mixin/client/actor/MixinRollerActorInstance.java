package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.contraptions.actors.roller.RollerActorVisual;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(RollerActorVisual.class)
public class MixinRollerActorInstance implements ActorInstanceDuck {
    @Shadow(remap = false)
    TransformedInstance frame;

    @Override
    public @NotNull List<AbstractInstance> ci$getInstances() {
        return ImmutableList.of(frame);
    }
}
