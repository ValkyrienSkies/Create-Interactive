package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.jozufozu.flywheel.api.InstanceData;
import com.simibubi.create.content.contraptions.render.ActorInstance;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;

import java.util.Collections;
import java.util.List;

@Mixin(ActorInstance.class)
public class MixinActorInstance implements ActorInstanceDuck {
    @Override
    public @NotNull List<InstanceData> ci$getInstances() {
        return Collections.EMPTY_LIST;
    }
}
