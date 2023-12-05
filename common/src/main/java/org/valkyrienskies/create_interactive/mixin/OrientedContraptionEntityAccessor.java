package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OrientedContraptionEntity.class)
public interface OrientedContraptionEntityAccessor {
    @Accessor("prevYaw")
    void setPrevYaw(float prevYaw);

    @Accessor("yaw")
    void setYaw(float yaw);

    @Accessor("pitch")
    void setPitch(float pitch);
}
