package org.valkyrienskies.create_interactive.mixinducks;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ContraptionDuck {
    void ci$setBlock(BlockPos localPos, StructureTemplate.StructureBlockInfo structureBlockInfo);

    @SuppressWarnings("unused")
    boolean ci$hasActorAtPos(BlockPos localPos);

    @SuppressWarnings("unused")
    boolean ci$hasBogeyAtPos(BlockPos localPos);

    @SuppressWarnings("unused")
    @Nullable
    Pair<StructureTemplate.StructureBlockInfo, MovementContext> ci$getActorAtPos(BlockPos localPos);

    @SuppressWarnings("unused")
    Collection<BlockPos> ci$getChangedActors();

    @SuppressWarnings("unused")
    void ci$clearChangedActors();
}
