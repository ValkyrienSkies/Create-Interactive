package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity.ContraptionRotationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContraptionRotationState.class)
public interface ContraptionRotationStateAccessor {
    @Accessor
    float getXRotation();
    @Accessor
    float getYRotation();
    @Accessor
    float getZRotation();
}
