package org.valkyrienskies.create_interactive.mixin.client;

import com.jozufozu.flywheel.api.InstanceData;
import com.simibubi.create.content.contraptions.render.ActorInstance;
import com.simibubi.create.content.contraptions.render.ContraptionInstanceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionInstanceManagerDuck;

import java.util.ArrayList;
import java.util.List;

@Mixin(ContraptionInstanceManager.class)
public class MixinContraptionInstanceManager implements ContraptionInstanceManagerDuck {
    @Shadow
    protected ArrayList<ActorInstance> actors;

    @Override
    public void deleteActorInstance(final ActorInstance actorInstance) {
        actors.remove(actorInstance);
        final List<InstanceData> instanceDataList = ((ActorInstanceDuck) actorInstance).ci$getInstances();
        for (final InstanceData instanceData : instanceDataList) {
            if (instanceData.isRemoved()) continue;
            instanceData.delete();
        }
    }
}
