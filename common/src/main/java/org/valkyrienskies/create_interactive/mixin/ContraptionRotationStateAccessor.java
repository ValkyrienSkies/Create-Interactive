package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity.ContraptionRotationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContraptionRotationState.class)
public interface ContraptionRotationStateAccessor {
    @Accessor(value = "xRotation", remap = false)
    float getXRotation();
    @Accessor(value = "yRotation", remap = false)
    float getYRotation();
    @Accessor(value = "zRotation", remap = false)
    float getZRotation();
}
