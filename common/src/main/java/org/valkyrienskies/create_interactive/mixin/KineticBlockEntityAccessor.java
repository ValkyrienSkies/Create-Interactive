package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KineticBlockEntity.class)
public interface KineticBlockEntityAccessor {

    @Accessor("flickerTally")
    int getFlickerTally();

    @Accessor("flickerTally")
    void setFlickerTally(int flickerTally);

    @Accessor("validationCountdown")
    int getValidationCountdown();

    @Accessor("validationCountdown")
    void setValidationCountdown(int validationCountdown);

    @Invoker("validateKinetics")
    void invokeValidateKinetics();

    @Accessor("preventSpeedUpdate")
    void setPreventSpeedUpdate(int preventSpeedUpdate);

    @Accessor("networkDirty")
    void setNetworkDirty(boolean networkDirty);

    @Accessor("networkDirty")
    boolean getNetworkDirty();

    @Accessor("sequenceContext")
    SequencedGearshiftBlockEntity.SequenceContext getSequenceContext();
}
