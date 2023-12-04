package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContraptionEntity.ContraptionRotationState.class)
public interface ContraptionRotationStateAccessor {
    @Accessor("xRotation")
    void setXRotation(float xRot);

    @Accessor("yRotation")
    void setYRotation(float yRot);

    @Accessor("zRotation")
    void setZRotation(float zRot);
}
