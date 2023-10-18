package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Mixin(Contraption.class)
public class MixinContraption {
    @Unique
    private Long vs$shipId = null;
    @Unique
    private final Map<BlockPos, Pair<StructureBlockInfo, BlockEntity>> vs$initialBlocks = new HashMap<>();
    @Shadow
    public AbstractContraptionEntity entity;
    @Shadow
    public BlockPos anchor;

    @Inject(method = "onEntityCreated", at = @At("HEAD"), remap = false)
    private void preOnEntityCreated(final AbstractContraptionEntity entity, final CallbackInfo ci) {
        final Level level = entity.level;
        if (level.isClientSide) {
            return;
        }
        final BlockPos blockPos = new BlockPos(entity.position());
        final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewShipAtBlock(VectorConversionsMCKt.toJOML(blockPos), false, 1.0, VSGameUtilsKt.getDimensionId(level));
        vs$shipId = serverShip.getId();

        // Anchor at ship center
        final Vector3ic shipCenter = serverShip.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i());

        for (final Entry<BlockPos, Pair<StructureBlockInfo, BlockEntity>> blockPosPairEntry : vs$initialBlocks.entrySet()) {
            final BlockPos pos = blockPosPairEntry.getKey();
            final Pair<StructureBlockInfo, BlockEntity> pair = blockPosPairEntry.getValue();
            final BlockPos localPos = pos.subtract(anchor);
            final BlockPos newPos = localPos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z());
            final int flags = Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE;
            level.setBlock(newPos, pair.getKey().state, flags);
        }

        ((AbstractContraptionEntityDuck) entity).setShadowShipId(vs$shipId);
    }

    @Inject(method = "addBlock", at = @At("HEAD"))
    private void preAddBlock(final BlockPos pos, final Pair<StructureBlockInfo, BlockEntity> pair, final CallbackInfo ci) {
        if (vs$initialBlocks.containsKey(pos)) {
            // Skip
            return;
        }
        vs$initialBlocks.put(pos, pair);
    }
}
