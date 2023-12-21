package org.valkyrienskies.create_interactive.content.buffer_stop

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class BufferStopBlockEntity(
    type: BlockEntityType<out BufferStopBlockEntity>, pos: BlockPos, state: BlockState
): BlockEntity(type, pos, state) {
}
