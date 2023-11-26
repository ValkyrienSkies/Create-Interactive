package org.valkyrienskies.create_interactive.mixin.kinetics;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.content.MechanicalPropagatorBearingBlockEntity;
import org.valkyrienskies.create_interactive.mixin.contraptins.ControlledContraptionEntityAccessor;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.List;

@Mixin(RotationPropagator.class)
public class MixinRotationPropagator {
    @Inject(method = "getPotentialNeighbourLocations", at = @At("RETURN"), remap = false)
    private static void postGetPotentialNeighbourLocations(final KineticBlockEntity be, final CallbackInfoReturnable<List<BlockPos>> cir) {
        final Ship ship = VSGameUtilsKt.getShipManagingPos(be.getLevel(), be.getBlockPos());
        if (ship == null) return;

        final AbstractContraptionEntity contraptionEntity = CreateInteractiveUtil.INSTANCE.getContraptionEntityForShip(ship.getId(), be.getLevel().isClientSide);
        if (!(contraptionEntity instanceof final ControlledContraptionEntity controlledContraptionEntity)) return;

        final BlockPos centerPos = VectorConversionsMCKt.toBlockPos(CreateInteractiveUtil.INSTANCE.getChunkClaimCenterPos(ship, be.getLevel()));
        if (!be.getBlockPos().equals(centerPos)) return;

        // Get the pos of the contraption bearing
        final IControlContraption controller = ((ControlledContraptionEntityAccessor) controlledContraptionEntity).invokeGetController();
        if (controller == null) return;

        // Add the pos if its tile is a MechanicalPropagatorBearingBlockEntity
        final BlockEntity blockEntity = be.getLevel().getBlockEntity(controller.getBlockPosition());
        if (!(blockEntity instanceof MechanicalPropagatorBearingBlockEntity)) return;

        cir.getReturnValue().add(controller.getBlockPosition());
    }
}
