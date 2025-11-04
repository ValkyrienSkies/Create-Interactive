package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.contraptions.actors.harvester.HarvesterActorVisual;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.List;

@Mixin(HarvesterActorVisual.class)
public class MixinHarvesterActorInstance implements ActorInstanceDuck {
    @Shadow(remap = false)
    protected TransformedInstance harvester;

    @Override
    public @NotNull List<AbstractInstance> ci$getInstances() {
        return ImmutableList.of(harvester);
    }
}
