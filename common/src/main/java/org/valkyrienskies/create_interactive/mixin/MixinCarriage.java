package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.create_interactive.mixinducks.CarriageDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.Map;

@Mixin(Carriage.class)
public class MixinCarriage implements CarriageDuck {
    // The dimension we set the ship for this carriage to be in
    @Unique
    private ResourceKey<Level> ci$shipDimension;
    @Unique
    private Long vs$shadowShipId = null;

    @Shadow
    private Map<ResourceKey<Level>, Carriage.DimensionalCarriageEntity> entities;

    @Override
    public boolean ci$doesCarriageEntityControlShip(final CarriageContraptionEntity entity, final Long shadowShipId) {
        if (shadowShipId == null) {
            return false;
        }
        if (vs$shadowShipId == null) {
            vs$shadowShipId = shadowShipId;
        } else if (!vs$shadowShipId.equals(shadowShipId)) {
            throw new IllegalStateException("ShadowShipId mismatch!");
        }
        if (ci$shipDimension == null) {
            ci$shipDimension = entity.level.dimension();
            return true;
        }
        return ci$shipDimension.equals(entity.level.dimension());
    }

    @Inject(method = "manageEntities", at = @At("RETURN"))
    private void postManageEntities(final Level level, final CallbackInfo ci) {
        if (level.isClientSide || vs$shadowShipId == null) return;

        if (!entities.containsKey(ci$shipDimension)) {
            // Choose a new entity to give ownership of the ship
            for (final Map.Entry<ResourceKey<Level>, Carriage.DimensionalCarriageEntity> newOwnerEntry : entities.entrySet()) {
                final AbstractContraptionEntity actualEntity = newOwnerEntry.getValue().entity.get();
                if (actualEntity != null) {
                    // Set this entity to be the shipowner
                    ci$shipDimension = newOwnerEntry.getKey();
                    ((AbstractContraptionEntityDuck) actualEntity).setShadowShipId(vs$shadowShipId);
                    return;
                }
            }
            // We can't find an entity to attach this to? Just move the ship anyway.
            final Map.Entry<ResourceKey<Level>, Carriage.DimensionalCarriageEntity> destination = entities.entrySet().iterator().next();
            ci$shipDimension = destination.getKey();

            // This is fine because there is only 1 ship object world per server, so any level is valid for this
            final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getAllShips().getById(vs$shadowShipId);
            if (serverShip == null) {
                return;
            }

            final Vector3dc pos = VectorConversionsMCKt.toJOML(destination.getValue().positionAnchor);
            // Not sure what to do for rot tbh, but this will work for now
            final Quaterniondc rot = new Quaterniond();
            final CreateInteractiveUtil.ContraptionPosRot posRot = new CreateInteractiveUtil.ContraptionPosRot(pos, rot);
            final ServerLevel destLevel = level.getServer().getLevel(ci$shipDimension);
            CreateInteractiveUtil.INSTANCE.teleportShipToPosRot(posRot, serverShip, destLevel);
        }
    }
}
