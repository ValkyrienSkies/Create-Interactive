package org.valkyrienskies.create_interactive.mixin.client;

import com.jozufozu.flywheel.backend.instancing.AbstractInstance;
import com.jozufozu.flywheel.backend.instancing.InstanceManager;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.lang.ref.WeakReference;

@Mixin(InstanceManager.class)
public class MixinInstanceManager {
    @Unique
    private boolean ci$shouldRemoveBlockEntityInShip(final @NotNull BlockEntity blockEntity) {
        final Level level = blockEntity.getLevel();
        final BlockPos pos = blockEntity.getBlockPos();
        final Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) return false;
        final WeakReference<AbstractContraptionEntity> contraptionEntityWeakReference = CreateInteractiveUtil.INSTANCE.getShipIdToContraptionEntityClient().get(ship.getId());
        if (contraptionEntityWeakReference == null) return false;
        final AbstractContraptionEntity contraptionEntity = contraptionEntityWeakReference.get();
        if (contraptionEntity == null) return false;

        final Vector3ic shipCenter = ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i());
        final BlockPos relativePos = pos.subtract(VectorConversionsMCKt.toBlockPos(shipCenter));

        return ((ContraptionDuck) contraptionEntity.getContraption()).ci$hasActorAtPos(relativePos, blockEntity instanceof MechanicalBearingBlockEntity);
    }

    @Inject(method = "createInternal", at = @At("HEAD"), cancellable = true, remap = false)
    private void preCreateInternal(final Object obj, final CallbackInfoReturnable<AbstractInstance> cir) {
        if (obj instanceof BlockEntity blockEntity && ci$shouldRemoveBlockEntityInShip(blockEntity)) {
            // Don't create the instance
            cir.setReturnValue(null);
        }
    }
}
