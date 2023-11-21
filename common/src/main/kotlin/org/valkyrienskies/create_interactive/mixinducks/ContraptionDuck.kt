package org.valkyrienskies.create_interactive.mixinducks

import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.valkyrienskies.create_interactive.CreateActorImmutable

interface ContraptionDuck {
    fun `ci$setBlock`(localPos: BlockPos, structureBlockInfo: StructureTemplate.StructureBlockInfo)

    fun `ci$hasActorAtPos`(localPos: BlockPos): Boolean

    fun `ci$hasBogeyAtPos`(localPos: BlockPos): Boolean

    fun `ci$getActorAtPos`(localPos: BlockPos): CreateActorImmutable?

    fun `ci$getChangedActors`(): Collection<BlockPos>

    fun `ci$clearChangedActors`()
}
