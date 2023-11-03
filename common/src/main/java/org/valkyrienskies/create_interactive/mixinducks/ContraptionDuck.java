package org.valkyrienskies.create_interactive.mixinducks;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ContraptionDuck {
    void ci$setBlock(BlockPos localPos, StructureTemplate.StructureBlockInfo structureBlockInfo);

    boolean ci$hasActorAtPos(BlockPos localPos, boolean isCheckingMechanicalBearing);

    @Nullable
    Pair<StructureTemplate.StructureBlockInfo, MovementContext> ci$getActorAtPos(BlockPos localPos);

    Collection<BlockPos> ci$getChangedActors();

    void ci$clearChangedActors();
}
