package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;

@Mixin(CarriageContraptionEntity.class)
public class MixinCarriageContraptionEntity {
    @Redirect(method = "tickContraption", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Carriage$DimensionalCarriageEntity;alignEntity(Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;)V"), remap = false)
    private void redirectAlignEntity(final Carriage.DimensionalCarriageEntity instance, final CarriageContraptionEntity lookahead) {
        if (!CreateInteractiveUtil.INSTANCE.isTrainDerailed(lookahead)) {
            instance.alignEntity(lookahead);
        }
    }
}
