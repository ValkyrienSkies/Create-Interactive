package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.simibubi.create.content.contraptions.actors.ActorInstance;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.Collections;
import java.util.List;

@Mixin(ActorInstance.class)
public class MixinActorInstance implements ActorInstanceDuck {
    @Override
    public @NotNull List<AbstractInstance> ci$getInstances() {
        return Collections.EMPTY_LIST;
    }
}
