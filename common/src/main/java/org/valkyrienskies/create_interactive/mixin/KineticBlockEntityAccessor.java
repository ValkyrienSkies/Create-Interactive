package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KineticBlockEntity.class)
public interface KineticBlockEntityAccessor {

    @Accessor(value = "flickerTally",remap = false)
    int getFlickerTally();

    @Accessor(value = "flickerTally",remap = false)
    void setFlickerTally(int flickerTally);

    @Accessor(value = "validationCountdown",remap = false)
    int getValidationCountdown();

    @Accessor(value = "validationCountdown",remap = false)
    void setValidationCountdown(int validationCountdown);

    @Invoker(value = "validateKinetics",remap = false)
    void invokeValidateKinetics();

    @Accessor(value = "preventSpeedUpdate",remap = false)
    void setPreventSpeedUpdate(int preventSpeedUpdate);

    @Accessor(value = "networkDirty",remap = false)
    void setNetworkDirty(boolean networkDirty);

    @Accessor(value = "networkDirty",remap = false)
    boolean getNetworkDirty();

    @Accessor(value = "sequenceContext",remap = false)
    SequencedGearshiftBlockEntity.SequenceContext getSequenceContext();
}
