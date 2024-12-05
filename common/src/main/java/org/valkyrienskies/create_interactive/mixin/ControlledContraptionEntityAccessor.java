package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ControlledContraptionEntity.class)
public interface ControlledContraptionEntityAccessor {
    @Accessor(value = "angleDelta",remap = false)
    float getAngleDelta();
}
