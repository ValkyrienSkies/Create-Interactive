package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MechanicalBearingBlockEntity.class)
public interface MechanicalBearingBlockEntityAccessor {
    @Accessor(value = "sequencedAngleLimit",remap = false)
    double getSequencedAngleLimit();

    @Accessor(value = "sequencedAngleLimit",remap = false)
    void setSequencedAngleLimit(double sequencedAngleLimit);

    @Accessor(value = "assembleNextTick",remap = false)
    boolean getAssembleNextTick();

    @Accessor(value = "assembleNextTick",remap = false)
    void setAssembleNextTick(boolean assembleNextTick);

    @Accessor(value = "angle",remap = false)
    void setAngle(float angle);

    @Accessor(value = "angle",remap = false)
    float getAngle();

    @Accessor(value = "clientAngleDiff", remap = false)
    float getClientAngleDiff();

    @Accessor(value = "clientAngleDiff",remap = false)
    void setClientAngleDiff(float clientAngleDiff);

    @Accessor(value = "movementMode",remap = false)
    ScrollOptionBehaviour<IControlContraption.RotationMode> getMovementMode();
}
