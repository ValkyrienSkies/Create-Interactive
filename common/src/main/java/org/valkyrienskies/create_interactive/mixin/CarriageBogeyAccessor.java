package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.CarriageBogey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CarriageBogey.class)
public interface CarriageBogeyAccessor {
    @Accessor("isLeading")
    boolean getIsLeading();
}
