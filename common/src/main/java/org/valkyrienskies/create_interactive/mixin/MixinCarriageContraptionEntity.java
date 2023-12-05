package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixin_logic.MixinCarriageContraptionEntityLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

@Mixin(CarriageContraptionEntity.class)
public abstract class MixinCarriageContraptionEntity extends OrientedContraptionEntity implements AbstractContraptionEntityDuck {
    public MixinCarriageContraptionEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Redirect(method = "tickContraption", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/Carriage$DimensionalCarriageEntity;alignEntity(Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;)V"), remap = false)
    private void redirectAlignEntity(final Carriage.DimensionalCarriageEntity instance, final CarriageContraptionEntity lookahead) {
        if (!CreateInteractiveUtil.INSTANCE.isTrainDerailed(lookahead)) {
            instance.alignEntity(lookahead);
        }
    }

    @Unique
    private boolean ci$hasFrontConstraint = false;

    /**
     * Create the constraints between train cars
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(final CallbackInfo ci) {
        if (level.isClientSide) {
            return;
        }
        if (!ci$hasFrontConstraint) {
            ci$hasFrontConstraint = MixinCarriageContraptionEntityLogic.INSTANCE.preTick$create_interactive(CarriageContraptionEntity.class.cast(this)) != null;
        }
    }
}
