package org.valkyrienskies.create_interactive.content

import com.simibubi.create.foundation.block.IBE
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import org.valkyrienskies.create_interactive.GameContent

object PropegatorBlock : PropegatingAxisBlock(
    Properties.of(Material.STONE)
), IBE<PropegatorBlockEntity> {

    override fun getBlockEntityClass(): Class<PropegatorBlockEntity> = PropegatorBlockEntity::class.java
    override fun getBlockEntityType(): BlockEntityType<out PropegatorBlockEntity> = GameContent.PROPEGATOR_BE.get()

    override fun hasShaftTowards(world: LevelReader, pos: BlockPos, state: BlockState, face: Direction): Boolean {
        return state.getValue(AXIS) != face.axis
    }
}