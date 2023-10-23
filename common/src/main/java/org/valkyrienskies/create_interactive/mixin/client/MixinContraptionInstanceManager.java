package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.render.ActorInstance;
import com.simibubi.create.content.contraptions.render.ContraptionInstanceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinContraptionInstanceManagerLogic;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionInstanceManagerDuck;

import java.util.ArrayList;

@Mixin(ContraptionInstanceManager.class)
public class MixinContraptionInstanceManager implements ContraptionInstanceManagerDuck {
    @Shadow
    protected ArrayList<ActorInstance> actors;

    @Override
    public void deleteActorInstance(final ActorInstance actorInstance) {
        MixinContraptionInstanceManagerLogic.INSTANCE.deleteActorInstance$create_interactive(actors, actorInstance);
    }
}
