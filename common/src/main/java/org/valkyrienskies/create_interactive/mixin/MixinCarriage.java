package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import kotlin.Unit;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinCarriageLogic;
import org.valkyrienskies.create_interactive.mixinducks.CarriageDuck;

import java.util.Map;

@Mixin(Carriage.class)
public class MixinCarriage implements CarriageDuck {
    // The dimension we set the ship for this carriage to be in
    @Unique
    private ResourceKey<Level> ci$shipDimension;
    @Unique
    private Long ci$shadowShipId = null;

    @Shadow
    private Map<ResourceKey<Level>, Carriage.DimensionalCarriageEntity> entities;

    @Override
    public boolean ci$doesCarriageEntityControlShip(final CarriageContraptionEntity entity, final Long shadowShipId) {
        return MixinCarriageLogic.INSTANCE.doesCarriageEntityControlShip$create_interactive(
            entity, shadowShipId, ci$shipDimension, ci$shadowShipId,
            (a) -> {
                ci$shadowShipId = a;
                return Unit.INSTANCE;
            },
            (a) -> {
                ci$shipDimension = a;
                return Unit.INSTANCE;
            }
        );
    }

    @Inject(method = "manageEntities", at = @At("RETURN"))
    private void postManageEntities(final Level level, final CallbackInfo ci) {
        MixinCarriageLogic.INSTANCE.postManageEntities$create_interactive(level, ci$shadowShipId, ci$shipDimension, entities, (a) -> {
            ci$shipDimension = a;
            return Unit.INSTANCE;
        });
    }
}
