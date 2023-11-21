package org.valkyrienskies.create_interactive.content

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import org.valkyrienskies.create_interactive.GameContent

abstract class PropegatingAxisBlock(properties: Properties) : RotatedPillarKineticBlock(properties) {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(GameContent.CONNECTED, false)
        )
    }

    override fun getRotationAxis(state: BlockState): Direction.Axis = state.getValue(AXIS)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(GameContent.CONNECTED)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val axisState = super.getStateForPlacement(context)!!
        return axisState.setValue(GameContent.CONNECTED, PropegatingTools.checkIfConnected(context.level, axisState, context.clickedPos, null))
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        val direction = Direction.fromNormal(fromPos.x - pos.x, fromPos.y - pos.y, fromPos.z - pos.z)
        val connected = PropegatingTools.checkIfConnected(level, state, pos, direction)
        if (connected != state.getValue(GameContent.CONNECTED)) {
            level.setBlockAndUpdate(pos, state.setValue(GameContent.CONNECTED, connected))
        }
    }
}