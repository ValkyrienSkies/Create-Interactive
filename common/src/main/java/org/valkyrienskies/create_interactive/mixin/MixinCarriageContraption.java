package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.CarriageContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CarriageContraption.class)
public class MixinCarriageContraption {

    @ModifyConstant(method = "assemble", constant = @org.spongepowered.asm.mixin.injection.Constant(intValue = 1, ordinal = 0), remap = false)
    private int allowEmptyContraption(int original) {
        return -1;
    }
}
