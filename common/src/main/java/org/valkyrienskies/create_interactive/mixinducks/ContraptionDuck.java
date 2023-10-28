package org.valkyrienskies.create_interactive.mixinducks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Collection;

public interface ContraptionDuck {
    void ci$setBlock(BlockPos localPos, StructureTemplate.StructureBlockInfo structureBlockInfo);

    boolean ci$hasActorAtPos(BlockPos localPos, boolean isCheckingMechanicalBearing);

    Collection<BlockPos> ci$getChangedActors();

    void ci$clearChangedActors();
}
