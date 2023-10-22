package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.AllInteractionBehaviours;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement;
import com.simibubi.create.content.contraptions.behaviour.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.simibubi.create.foundation.utility.NBTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.MutablePair;
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
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Mixin(Contraption.class)
public abstract class MixinContraption implements ContraptionDuck {
    @Unique
    private final Map<BlockPos, Pair<StructureBlockInfo, BlockEntity>> vs$initialBlocks = new HashMap<>();
    @Unique
    private final Set<BlockPos> ci$changedActors = new HashSet<>();
    @Shadow
    public BlockPos anchor;
    @Shadow(remap = false)
    public boolean disassembled;
    @Shadow(remap = false)
    public AbstractContraptionEntity entity;
    @Shadow(remap = false)
    protected Map<BlockPos, StructureBlockInfo> blocks;
    @Shadow(remap = false)
    public AABB bounds;
    @Shadow(remap = false)
    protected List<MutablePair<StructureBlockInfo, MovementContext>> actors;
    @Shadow(remap = false)
    protected Map<BlockPos, MovingInteractionBehaviour> interactors;
    @Shadow
    protected abstract void disableActorOnStart(final MovementContext context);
    @Shadow
    protected abstract CompoundTag getBlockEntityNBT(final Level world, final BlockPos pos);

    @Inject(method = "onEntityCreated", at = @At("HEAD"), remap = false)
    private void preOnEntityCreated(final AbstractContraptionEntity entity, final CallbackInfo ci) {
        final Level level = entity.level;
        if (level.isClientSide) {
            return;
        }
        final Long prevId = ((AbstractContraptionEntityDuck) entity).getShadowShipId();
        if (prevId != null && VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).getAllShips().getById(prevId) != null) {
            // If shadow ship already exists then don't make a new one
            return;
        }
        final BlockPos blockPos = new BlockPos(entity.position());
        final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) level).createNewShipAtBlock(VectorConversionsMCKt.toJOML(blockPos), false, 1.0, VSGameUtilsKt.getDimensionId(level));
        final Long shipId = serverShip.getId();

        // Anchor at ship center
        final Vector3ic shipCenter = serverShip.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i());

        for (final Entry<BlockPos, Pair<StructureBlockInfo, BlockEntity>> blockPosPairEntry : vs$initialBlocks.entrySet()) {
            final BlockPos pos = blockPosPairEntry.getKey();
            final Pair<StructureBlockInfo, BlockEntity> pair = blockPosPairEntry.getValue();
            final BlockPos localPos = pos.subtract(anchor);
            final BlockPos newPos = localPos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z());
            final int flags = Block.UPDATE_MOVE_BY_PISTON | Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE;
            level.setBlock(newPos, pair.getKey().state, flags);

            // region Copy the tile entity to the ship
            final BlockEntity blockEntity = pair.getValue();
            final BlockEntity newBlockEntity = level.getBlockEntity(newPos);

            if (blockEntity != null && newBlockEntity != null) {
                // Transform the block entity, put it in the ship
                CompoundTag tag = pair.getKey().nbt;
                tag = NBTProcessors.process(blockEntity, tag, false);
                if (tag != null) {
                    tag.putInt("x", newPos.getX());
                    tag.putInt("y", newPos.getY());
                    tag.putInt("z", newPos.getZ());

                    if (blockEntity instanceof IMultiBlockEntityContainer && tag.contains("LastKnownPos"))
                        tag.put("LastKnownPos", NbtUtils.writeBlockPos(BlockPos.ZERO.below(Integer.MAX_VALUE - 1)));

                    newBlockEntity.load(tag);
                    level.setBlockEntity(newBlockEntity);
                }
            }
            // endregion
        }

        ((AbstractContraptionEntityDuck) entity).setShadowShipId(shipId);
    }

    @Inject(method = "addBlock", at = @At("HEAD"))
    private void preAddBlock(final BlockPos pos, final Pair<StructureBlockInfo, BlockEntity> pair, final CallbackInfo ci) {
        if (vs$initialBlocks.containsKey(pos)) {
            // Skip
            return;
        }
        vs$initialBlocks.put(pos, pair);
    }

    @Inject(method = "addBlocksToWorld", at = @At("HEAD"))
    private void preAddBlocksToWorld(final Level world, final StructureTransform transform, final CallbackInfo ci) {
        if (disassembled) {
            // Do nothing
            return;
        }
        final AbstractContraptionEntity entityCopy = entity;
        if (entityCopy == null) {
            System.out.println("Susmogus!");
            return;
        }
        final AbstractContraptionEntityDuck duck = (AbstractContraptionEntityDuck) entityCopy;
        final Long shadowShipId = duck.getShadowShipId();
        if (shadowShipId == null) {
            return;
        }
        final Ship ship = VSGameUtilsKt.getShipObjectWorld(world).getAllShips().getById(shadowShipId);
        if (ship == null) {
            return;
        }
        // Anchor at ship center
        final Vector3ic shipCenter = ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(world), new Vector3i());

        // Update contraption tile entities to match the contents of the shadow ship
        ship.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
            final ChunkAccess chunkAccess = world.getChunk(chunkX, chunkZ);
            for (final BlockPos blockPos : chunkAccess.getBlockEntitiesPos()) {
                final BlockPos localPos = blockPos.offset(-shipCenter.x(), -shipCenter.y(), -shipCenter.z());
                final BlockState blockState = world.getBlockState(blockPos);
                final CompoundTag compoundTag = getBlockEntityNBT(world, blockPos);
                final StructureBlockInfo blockInfo = new StructureBlockInfo(localPos, blockState, compoundTag);
                blocks.put(localPos, blockInfo);
            }
        });
    }

    @Override
    public void ci$setBlock(final Level level, final BlockPos localPos, final StructureTemplate.StructureBlockInfo structureBlockInfo) {
        final StructureTemplate.StructureBlockInfo prevState = blocks.get(localPos);
        if (prevState != null && prevState.state == structureBlockInfo.state) {
            return;
        }

        blocks.put(localPos, structureBlockInfo);
        bounds = bounds.minmax(new AABB(localPos));

        if (AllMovementBehaviours.getBehaviour(structureBlockInfo.state) != null) {
            MovementContext context = new MovementContext(level, structureBlockInfo, Contraption.class.cast(this));
            MovementBehaviour behaviour = AllMovementBehaviours.getBehaviour(structureBlockInfo.state);
            if (behaviour != null)
                behaviour.startMoving(context);
            if (behaviour instanceof ContraptionControlsMovement)
                disableActorOnStart(context);

            actors.removeIf(next -> next.left.pos.equals(structureBlockInfo.pos));
            actors.add(MutablePair.of(structureBlockInfo, context));
            ci$changedActors.add(structureBlockInfo.pos);
        } else {
            // Remove actor if one exists
            final boolean anyRemoved = actors.removeIf(next -> next.left.pos.equals(structureBlockInfo.pos));
            if (anyRemoved) {
                ci$changedActors.add(structureBlockInfo.pos);
            }
        }

        MovingInteractionBehaviour interactionBehaviour = AllInteractionBehaviours.getBehaviour(structureBlockInfo.state);
        if (interactionBehaviour != null) {
            interactors.put(localPos, interactionBehaviour);
        } else {
            // Remove interactor if one exists
            interactors.remove(structureBlockInfo.pos);
        }
    }

    @Override
    public boolean ci$hasActorAtPos(final BlockPos localPos, boolean isCheckingMechanicalBearing) {
        for (final MutablePair<StructureBlockInfo, MovementContext> actor : actors) {
            if (actor.left.pos.equals(localPos)) {
                if (isCheckingMechanicalBearing) {
                    return actor.left.nbt != null;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<BlockPos> ci$getChangedActors() {
        return ci$changedActors;
    }

    @Override
    public void ci$clearChangedActors() {
        ci$changedActors.clear();
    }
}
