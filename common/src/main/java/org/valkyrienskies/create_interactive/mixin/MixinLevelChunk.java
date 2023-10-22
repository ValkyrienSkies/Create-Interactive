package org.valkyrienskies.create_interactive.mixin;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.backend.instancing.InstancedRenderDispatcher;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.Nullable;
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
public abstract class MixinLevelChunk extends ChunkAccess {
    @Shadow
    @Final
    Level level;

    public MixinLevelChunk(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable LevelChunkSection[] sections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, biomeRegistry, inhabitedTime, sections, blendingData);
    }

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
        } else {
            if (Backend.isOn() && ((ContraptionDuck) contraptionEntity.getContraption()).ci$hasActorAtPos(relativePos, false)) {
                final BlockEntity blockEntity = blockEntities.get(pos);
                if (blockEntity != null) {
                    // TODO: Need to do this in other places too
                    // Remove block entity flywheel instances if tile entity is on a shadow ship
                    InstancedRenderDispatcher.getBlockEntities(this.level).remove(blockEntity);
                }
            }
        }
    }
}
