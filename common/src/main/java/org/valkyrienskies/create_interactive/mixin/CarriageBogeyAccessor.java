package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CarriageBogey.class)
public interface CarriageBogeyAccessor {
    @Accessor("isLeading")
    boolean getIsLeading();

    @Accessor("yaw")
    LerpedFloat getYaw();

    @Accessor("pitch")
    LerpedFloat getPitch();

    @Accessor("type")
    AbstractBogeyBlock<?> getType();
}
