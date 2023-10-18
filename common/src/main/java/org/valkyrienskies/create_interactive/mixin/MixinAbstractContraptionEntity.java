package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(AbstractContraptionEntity.class)
public abstract class MixinAbstractContraptionEntity extends Entity implements AbstractContraptionEntityDuck {
    @Unique
    private Long vs$shadowShipId = null;

    @Shadow
    protected Contraption contraption;

    public MixinAbstractContraptionEntity(final EntityType<?> entityType, final Level level) {
        super(entityType, level);
    }

    @Override
    public void setShadowShipId(final Long shadowShipId) {
        vs$shadowShipId = shadowShipId;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void preTick(final CallbackInfo ci) {
        final Long shadowShipId = vs$shadowShipId;
        if (shadowShipId == null) {
            return;
        }
        if (level.isClientSide) {
            return;
        }
        final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getAllShips().getById(shadowShipId);
        if (serverShip != null) {
            // TODO: Do this more efficiently
            final ContraptionRotationStateAccessor rotationState = (ContraptionRotationStateAccessor) AbstractContraptionEntity.class.cast(this).getRotationState();
            final Quaterniondc newRot = new Quaterniond().rotateXYZ(Math.toRadians(rotationState.getXRotation()), Math.toRadians(rotationState.getYRotation()), Math.toRadians(rotationState.getZRotation()));

            // Anchor at ship center of mass
            final Vector3ic
                shipCenter = serverShip.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i());
            final Vector3d
                offset = serverShip.getInertiaData().getCenterOfMassInShip().sub(shipCenter.x(), shipCenter.y(), shipCenter.z(), new Vector3d());
            newRot.transform(offset);
            offset.add(0.5, 0.5, 0.5);

            final Vector3dc newPos = VectorConversionsMCKt.toJOML(position()).add(offset);
            final Vector3dc newVel = new Vector3d();
            final Vector3dc newOmega = new Vector3d();
            final String newDimension = VSGameUtilsKt.getDimensionId(level);
            final double newScale = 1.0;
            final ShipTeleportData shipTeleportData = new ShipTeleportDataImpl(
                newPos, newRot, newVel, newOmega, newDimension, newScale
            );
            VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).teleportShip(serverShip, shipTeleportData);
        } else {
            System.out.println("ERRRORRRRRRRRRR!!!!!!!!!");
        }
    }
}
