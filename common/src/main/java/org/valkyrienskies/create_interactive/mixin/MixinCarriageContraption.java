package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.InteractiveStorageManager;

@Mixin(CarriageContraption.class)
public abstract class MixinCarriageContraption extends Contraption {
    @Inject(method = "swapStorageAfterAssembly", at = @At("RETURN"), remap = false)
    private void postSwapStorageAfterAssembly(final CarriageContraptionEntity cce, final CallbackInfo ci) {
        if (!(storage instanceof InteractiveStorageManager)) {
            storage = new InteractiveStorageManager();
        }
    }
}
