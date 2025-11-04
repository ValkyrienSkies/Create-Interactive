package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ActorVisual.class)
public interface ActorVisualAccessor {
    @Accessor
    MovementContext getContext();
}
