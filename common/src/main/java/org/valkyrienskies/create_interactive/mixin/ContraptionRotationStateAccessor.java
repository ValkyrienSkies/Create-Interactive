package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity.ContraptionRotationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContraptionRotationState.class)
public interface ContraptionRotationStateAccessor {
    @Accessor(remap = false)
    float getXRotation();
    @Accessor(remap = false)
    float getYRotation();
    @Accessor(remap = false)
    float getZRotation();
}
