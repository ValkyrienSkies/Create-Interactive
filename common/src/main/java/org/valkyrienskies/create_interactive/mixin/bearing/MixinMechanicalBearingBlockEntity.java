package org.valkyrienskies.create_interactive.mixin.bearing;

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.bearing.MechanicalBearingBlockEntityLogic;

@Mixin(MechanicalBearingBlockEntity.class)
public class MixinMechanicalBearingBlockEntity {
    @Inject(method = "getInterpolatedAngle", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetInterpolatedAngle(final float partialTicks, final CallbackInfoReturnable<Float> cir) {
        MechanicalBearingBlockEntityLogic.INSTANCE.preGetInterpolatedAngle$create_interactive(
            MechanicalBearingBlockEntity.class.cast(this), cir
        );
    }
}
