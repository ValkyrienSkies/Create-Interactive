package org.valkyrienskies.create_interactive.mixinducks;

import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ActorInstanceDuck {
    @NotNull
    List<AbstractInstance> ci$getInstances();
}
