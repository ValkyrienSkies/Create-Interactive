package org.valkyrienskies.create_interactive.fabric.mixin;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageEntityHandler;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinCarriageEntityHandlerLogic;

@Mixin(CarriageEntityHandler.class)
public class MixinCarriageEntityHandler {
    @Inject(method = "onEntityEnterSection", at = @At("HEAD"), cancellable = true)
    private static void preOnEntityEnterSection(Entity entity, long packedOldPos, long packedNewPos, CallbackInfo ci) {
        MixinCarriageEntityHandlerLogic.INSTANCE.preOnEntityEnterSection(entity, ci);
    }

    @Inject(method = "validateCarriageEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preValidateCarriageEntity(CarriageContraptionEntity entity, CallbackInfo ci) {
        MixinCarriageEntityHandlerLogic.INSTANCE.preValidateCarriageEntity(entity, ci);
    }
}
