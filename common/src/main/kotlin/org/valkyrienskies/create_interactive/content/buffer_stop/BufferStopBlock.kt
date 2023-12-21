package org.valkyrienskies.create_interactive.content.buffer_stop

import com.simibubi.create.foundation.block.IBE
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.services.NoOptimize

class BufferStopBlock(properties: Properties): HorizontalDirectionalBlock(properties), IBE<BufferStopBlockEntity> {
    @NoOptimize
    override fun getBlockEntityClass(): Class<BufferStopBlockEntity> = BufferStopBlockEntity::class.java

    @NoOptimize
    override fun getBlockEntityType(): BlockEntityType<out BufferStopBlockEntity> = GameContent.BUFFER_STOP_BE.get()

    @NoOptimize
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }

    @NoOptimize
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val direction = context.horizontalDirection
        return defaultBlockState().setValue(FACING, direction) as BlockState
    }

    /**
     * Don't clip the sides of blocks
     */
    @NoOptimize
    override fun getVisualShape(
        state: BlockState?,
        level: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?,
    ): VoxelShape {
        return Shapes.empty()
    }
}
