package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.Direction;
import org.joml.Quaterniond;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.mixin_logic.MixinCBCContraptionRotationStateLogic;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionRotationStateDuck;
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption;
import rbasamoyai.createbigcannons.cannon_control.contraption.CBCContraptionRotationState;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

@Mixin(CBCContraptionRotationState.class)
public class MixinCBCContraptionRotationState implements ContraptionRotationStateDuck {
    @Shadow(remap = false)
    @Final
    private PitchOrientedContraptionEntity entity;
    @Shadow(remap = false)
    private float yaw;
    @Override
    public Quaterniond ci$getRotationQuaternion(final Quaterniond dest) {
        boolean flag = ((AbstractMountedCannonContraption) this.entity.getContraption()).initialOrientation().getAxis() == Direction.Axis.X;
        float yawAdjust = this.yaw + (flag ? 180.0f : 0.0f);
        float pitch = this.entity.pitch;
        return MixinCBCContraptionRotationStateLogic.INSTANCE.getRotationQuaternion$create_interactive(
            AbstractContraptionEntity.ContraptionRotationState.class.cast(this), flag, yawAdjust, pitch, dest
        );
    }
}
