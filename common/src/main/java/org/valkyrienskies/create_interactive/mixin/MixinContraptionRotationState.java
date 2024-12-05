package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import org.joml.Quaterniond;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixin_logic.MixinContraptionRotationStateLogic;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionRotationStateDuck;

@Mixin(AbstractContraptionEntity.ContraptionRotationState.class)
public class MixinContraptionRotationState implements ContraptionRotationStateDuck {
    @Shadow(remap = false)
    public float xRotation;
    @Shadow(remap = false)
    public float yRotation;
    @Shadow(remap = false)
    public float zRotation;

    @Override
    public Quaterniond ci$getRotationQuaternion(final Quaterniond dest) {
        return MixinContraptionRotationStateLogic.INSTANCE.getRotationQuaternion$create_interactive(
            AbstractContraptionEntity.ContraptionRotationState.class.cast(this),
            xRotation,
            yRotation,
            zRotation,
            dest
        );
    }
}
