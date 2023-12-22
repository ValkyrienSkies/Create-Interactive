package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KineticBlockEntity.class)
public interface KineticBlockEntityAccessor {

    @Accessor("flickerTally")
    public int getFlickerTally();

    @Accessor("flickerTally")
    public void setFlickerTally(int flickerTally);

    @Accessor("validationCountdown")
    public int getValidationCountdown();

    @Accessor("validationCountdown")
    public void setValidationCountdown(int validationCountdown);

    @Invoker("validateKinetics")
    void invokeValidateKinetics();
}
