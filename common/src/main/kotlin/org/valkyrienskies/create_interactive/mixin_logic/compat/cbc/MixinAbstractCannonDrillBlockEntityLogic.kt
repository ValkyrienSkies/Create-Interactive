package org.valkyrienskies.create_interactive.mixin_logic.compat.cbc

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld

internal object MixinAbstractCannonDrillBlockEntityLogic {
    internal fun onModifyContraptionBlock(
        latheEntity: AbstractContraptionEntity,
        blocks: MutableMap<BlockPos, StructureTemplate.StructureBlockInfo>,
        boringOffset: Any,
        newInfo: Any
    ): Any? {
        val relativePos = boringOffset as BlockPos
        val newInfo2 = newInfo as StructureTemplate.StructureBlockInfo
        val shipId = (latheEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`()
        if (shipId != null) {
            val level: Level = latheEntity.level()
            val ship = level.shipObjectWorld.allShips.getById(shipId)
            if (ship != null) {
                val centerPos = ship.getChunkClaimCenterPos(level)
                val blockPos = BlockPos(
                    centerPos.x() + relativePos.x,
                    centerPos.y() + relativePos.y,
                    centerPos.z() + relativePos.z
                )
                val flags = Block.UPDATE_MOVE_BY_PISTON or Block.UPDATE_ALL
                level.setBlock(blockPos, newInfo2.state, flags)
            }
        }
        return blocks.put(relativePos, newInfo2)
    }
}
