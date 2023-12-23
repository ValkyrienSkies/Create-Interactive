package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MechanicalBearingBlockEntity.class)
public interface MechanicalBearingBlockEntityAccessor {
    @Accessor("sequencedAngleLimit")
    double getSequencedAngleLimit();

    @Accessor("sequencedAngleLimit")
    void setSequencedAngleLimit(double sequencedAngleLimit);

    @Accessor("assembleNextTick")
    boolean getAssembleNextTick();

    @Accessor("assembleNextTick")
    void setAssembleNextTick(boolean assembleNextTick);

    @Accessor("angle")
    void setAngle(float angle);

    @Accessor("angle")
    float getAngle();

    @Accessor("clientAngleDiff")
    float getClientAngleDiff();

    @Accessor("clientAngleDiff")
    void setClientAngleDiff(float clientAngleDiff);

    @Accessor("movementMode")
    ScrollOptionBehaviour<IControlContraption.RotationMode> getMovementMode();
}
