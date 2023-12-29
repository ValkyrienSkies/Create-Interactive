package org.valkyrienskies.create_interactive.forge.mixin;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageEntityHandler;
import net.minecraftforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinCarriageEntityHandlerLogic;

@Mixin(CarriageEntityHandler.class)
public class MixinCarriageEntityHandler {
    @Inject(method = "onEntityEnterSection", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preOnEntityEnterSection(EntityEvent.EnteringSection event, CallbackInfo ci) {
        MixinCarriageEntityHandlerLogic.INSTANCE.preOnEntityEnterSection(event.getEntity(), ci);
    }

    @Inject(method = "validateCarriageEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preValidateCarriageEntity(CarriageContraptionEntity entity, CallbackInfo ci) {
        MixinCarriageEntityHandlerLogic.INSTANCE.preValidateCarriageEntity(entity, ci);
    }
}
