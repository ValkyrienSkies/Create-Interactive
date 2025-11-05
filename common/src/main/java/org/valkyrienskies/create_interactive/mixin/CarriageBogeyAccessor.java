package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import net.createmod.catnip.animation.LerpedFloat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CarriageBogey.class)
public interface CarriageBogeyAccessor {
    @Accessor(value = "isLeading",remap = false)
    boolean getIsLeading();

    @Accessor(value = "yaw",remap = false)
    LerpedFloat getYaw();

    @Accessor(value = "pitch",remap = false)
    LerpedFloat getPitch();

    @Accessor(value = "type",remap = false)
    AbstractBogeyBlock<?> getType();
}
