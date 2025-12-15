package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.server.level.ServerLevel;
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
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;

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


    /**
     * Create the constraints between train cars
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(final CallbackInfo ci) {
        if (level().isClientSide) {
            return;
        }
        final CarriageContraptionEntity thisAs = CarriageContraptionEntity.class.cast(this);
        Integer ci$jointId = MixinCarriageContraptionEntityLogic.INSTANCE.getJointId(thisAs);
        if (CreateInteractiveUtil.INSTANCE.isTrainDerailed(thisAs)) {
            if (ci$jointId == null) {
                // Compute this just in time to account for the distance between cars changing slightly during turns
                MixinCarriageContraptionEntityLogic.INSTANCE.preTick$create_interactive(thisAs);
            }
        } else {
            if (ci$jointId != null) {
                ValkyrienSkiesMod.getOrCreateGTPA(VSGameUtilsKt.getDimensionId(level())).removeJoint(ci$jointId);
            }
        }
    }
}
