package org.valkyrienskies.create_interactive.mixinducks;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.create_interactive.mixin.ContraptionRotationStateAccessor;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class DebugDuck {
    public static void doDuck(final AbstractContraptionEntity entity) {
        final Long shadowShipId = ((AbstractContraptionEntityDuck) entity).getShadowShipId();
        if (shadowShipId == null) {
            return;
        }
        final Level level = entity.level;
        if (level.isClientSide) {
            return;
        }
        final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getAllShips().getById(shadowShipId);
        if (serverShip != null) {
            // TODO: Do this more efficiently
            final AbstractContraptionEntity.ContraptionRotationState rotationStateOriginal = AbstractContraptionEntity.class.cast(entity).getRotationState();
            final ContraptionRotationStateAccessor rotationState = (ContraptionRotationStateAccessor) rotationStateOriginal;
            final Quaterniond newRot = new Quaterniond().rotateXYZ(Math.toRadians(rotationState.getXRotation()), Math.toRadians(rotationState.getYRotation()), Math.toRadians(rotationState.getZRotation()));

            newRot.rotateLocalY(Math.toRadians(rotationStateOriginal.getYawOffset()));

            // Anchor at ship center of mass
            final Vector3ic
                    shipCenter = serverShip.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i());
            final Vector3d
                    offset = serverShip.getInertiaData().getCenterOfMassInShip().sub(shipCenter.x(), shipCenter.y(), shipCenter.z(), new Vector3d());
            newRot.transform(offset);
            offset.add(0.5, 0.5, 0.5);

            final Vector3dc newPos = VectorConversionsMCKt.toJOML(entity.getAnchorVec()).add(offset);
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
