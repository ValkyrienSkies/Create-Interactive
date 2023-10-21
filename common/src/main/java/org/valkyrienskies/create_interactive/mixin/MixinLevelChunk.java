package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.lang.ref.WeakReference;

@Mixin(LevelChunk.class)
public class MixinLevelChunk {
    @Shadow
    @Final
    Level level;
    @Inject(method = "setBlockState", at = @At("RETURN"))
    public void postSetBlockState(final BlockPos pos, final BlockState state, final boolean moved,
                                  final CallbackInfoReturnable<BlockState> cir) {
        final Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (ship == null) return;
        final WeakReference<AbstractContraptionEntity> contraptionEntityWeakReference;
        if (level.isClientSide) {
            contraptionEntityWeakReference = CreateInteractiveUtil.INSTANCE.getShipIdToContraptionEntityClient().get(ship.getId());
        } else {
            contraptionEntityWeakReference = CreateInteractiveUtil.INSTANCE.getShipIdToContraptionEntityServer().get(ship.getId());
        }

        if (contraptionEntityWeakReference == null) return;
        final AbstractContraptionEntity contraptionEntity = contraptionEntityWeakReference.get();
        if (contraptionEntity == null) return;

        // Anchor at ship center
        final Vector3ic shipCenter = ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i());
        final BlockPos relativePos = pos.subtract(VectorConversionsMCKt.toBlockPos(shipCenter));

        // Set the block
        final StructureTemplate.StructureBlockInfo info = new StructureTemplate.StructureBlockInfo(relativePos, state, null);
        ((ContraptionDuck) contraptionEntity.getContraption()).ci$setBlock(level, relativePos, info);
        if (!level.isClientSide) {
            contraptionEntity.setBlock(relativePos, new StructureTemplate.StructureBlockInfo(relativePos, state, null));
        }
    }
}
