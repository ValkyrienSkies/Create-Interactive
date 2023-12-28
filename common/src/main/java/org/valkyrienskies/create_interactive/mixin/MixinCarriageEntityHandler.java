package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageEntityHandler;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CarriageEntityHandler.class)
public class MixinCarriageEntityHandler {
    // TODO: Only skip these if the carriage entity has relocated recently
    @Inject(method = "onEntityEnterSection", at = @At("HEAD"), cancellable = true)
    private static void preOnEntityEnterSection(Entity entity, long packedOldPos, long packedNewPos, CallbackInfo ci) {
        if (!(entity instanceof CarriageContraptionEntity))
            return;
        ci.cancel();
    }

    // TODO: Only skip these if the carriage entity has relocated recently
    @Inject(method = "validateCarriageEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preValidateCarriageEntity(CarriageContraptionEntity entity, CallbackInfo ci) {
        if (!entity.isAlive())
            return;
        ci.cancel();
    }
}
