package org.valkyrienskies.create_interactive.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.lang.ref.WeakReference;

/**
 * Disable rendering of block entities with actors in contraption shadow ships
 */
@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void preRender(final E blockEntity, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final CallbackInfo ci) {
        final Level level = blockEntity.getLevel();
        final BlockPos pos = blockEntity.getBlockPos();
        final Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) return;

        final WeakReference<AbstractContraptionEntity> contraptionEntityWeakReference = CreateInteractiveUtil.INSTANCE.getShipIdToContraptionEntityClient().get(ship.getId());
        if (contraptionEntityWeakReference == null) return;
        final AbstractContraptionEntity contraptionEntity = contraptionEntityWeakReference.get();
        if (contraptionEntity == null) return;

        // Anchor at ship center
        final Vector3ic shipCenter = ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i());
        final BlockPos relativePos = pos.subtract(VectorConversionsMCKt.toBlockPos(shipCenter));

        if (blockEntity instanceof StandardBogeyBlockEntity || ((ContraptionDuck) contraptionEntity.getContraption()).ci$hasActorAtPos(relativePos, blockEntity instanceof MechanicalBearingBlockEntity)) {
            // Cancel the rendering
            ci.cancel();
        }
    }
}
