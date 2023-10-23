package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinContraptionLogic;
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(Contraption.class)
public abstract class MixinContraption implements ContraptionDuck {
    @Unique
    private Map<BlockPos, Pair<StructureBlockInfo, BlockEntity>> vs$initialBlocks;
    @Unique
    private Set<BlockPos> ci$changedActors;
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

    @Inject(method = "<init>", at = @At("RETURN"))
    private void postInit(final CallbackInfo ci) {
        vs$initialBlocks = new HashMap<>();
        ci$changedActors = new HashSet<>();
    }

    @Inject(method = "onEntityCreated", at = @At("HEAD"), remap = false)
    private void preOnEntityCreated(final AbstractContraptionEntity entity, final CallbackInfo ci) {
        MixinContraptionLogic.INSTANCE.preOnEntityCreated$create_interactive(vs$initialBlocks, anchor, entity);
    }

    @Inject(method = "addBlock", at = @At("HEAD"))
    private void preAddBlock(final BlockPos pos, final Pair<StructureBlockInfo, BlockEntity> pair, final CallbackInfo ci) {
        MixinContraptionLogic.INSTANCE.preAddBlock$create_interactive(vs$initialBlocks, anchor, pair);
    }

    @Inject(method = "addBlocksToWorld", at = @At("HEAD"))
    private void preAddBlocksToWorld(final Level world, final StructureTransform transform, final CallbackInfo ci) {
        MixinContraptionLogic.INSTANCE.preAddBlocksToWorld$create_interactive(
            disassembled,
            entity,
            blocks,
            world,
            this::getBlockEntityNBT
        );
    }

    @Override
    public void ci$setBlock(final Level level, final BlockPos localPos, final StructureTemplate.StructureBlockInfo structureBlockInfo) {
        MixinContraptionLogic.INSTANCE.setBlock$create_interactive(
            blocks,
            actors,
            bounds,
            level,
            localPos,
            structureBlockInfo,
            (a) -> {
                bounds = a;
                return Unit.INSTANCE;
            },
            (a) -> {
                disableActorOnStart(a);
                return Unit.INSTANCE;
            },
            ci$changedActors,
            interactors,
            Contraption.class.cast(this)
        );
    }

    @Override
    public boolean ci$hasActorAtPos(final BlockPos localPos, boolean isCheckingMechanicalBearing) {
        return MixinContraptionLogic.INSTANCE.hasActorAtPos$create_interactive(localPos, isCheckingMechanicalBearing, actors);
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
