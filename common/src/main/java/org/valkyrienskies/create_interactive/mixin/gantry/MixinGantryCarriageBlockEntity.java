package org.valkyrienskies.create_interactive.mixin.gantry;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.gantry.GantryCarriageBlockEntity;
import com.simibubi.create.content.contraptions.gantry.GantryContraptionEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(GantryCarriageBlockEntity.class)
public abstract class MixinGantryCarriageBlockEntity extends KineticBlockEntity {
    public MixinGantryCarriageBlockEntity(final BlockEntityType<?> typeIn, final BlockPos pos, final BlockState state) {
        super(typeIn, pos, state);
    }

    /**
     * @author Triode
     * @reason Fix Gantry crashing on VS2 ships
     */
    @Inject(method = "initialize", at = @At("HEAD"), cancellable = true, remap = false)
    private void preInitialize(final CallbackInfo ci) {
        final Ship ship = VSGameUtilsKt.getShipManagingPos(level, worldPosition);
        if (ship == null) {
            return;
        }
        final AbstractContraptionEntity contraptionEntity = CreateInteractiveUtil.INSTANCE.getContraptionEntityForShip(ship.getId(), level.isClientSide);
        if (contraptionEntity == null) {
            return;
        }
        // Don't tick this block on other gantry contraptions
        if (contraptionEntity instanceof GantryContraptionEntity) {
            final Vector3ic shipCenter = CreateInteractiveUtil.INSTANCE.getChunkClaimCenterPos(ship, level);
            final BlockPos relativePos = getBlockPos().subtract(VectorConversionsMCKt.toBlockPos(shipCenter));
            if (relativePos.equals(BlockPos.ZERO)) {
                // Only partially run this on contraption ships
                super.initialize();
                ci.cancel();
            }
        }
    }
}
