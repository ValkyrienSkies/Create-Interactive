package org.valkyrienskies.create_interactive.content.propagating_axis

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.content.PropagatingTools
import org.valkyrienskies.create_interactive.directions
import org.valkyrienskies.create_interactive.services.NoOptimize

abstract class PropagatingAxisBlock(properties: Properties) : RotatedPillarKineticBlock(properties) {
    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(GameContent.CONNECTED, false)
        )
    }

    @NoOptimize
    override fun getRotationAxis(state: BlockState): Direction.Axis = state.getValue(AXIS)

    @NoOptimize
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(GameContent.CONNECTED)
    }

    @NoOptimize
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val axisState = super.getStateForPlacement(context)!!
        return axisState.setValue(GameContent.CONNECTED,
            PropagatingTools.checkIfConnected(context.level, axisState, context.clickedPos, null)
        )
    }

    @NoOptimize
    override fun onPlace(state: BlockState, worldIn: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, worldIn, pos, oldState, isMoving)

        if (worldIn.isClientSide) return
        if (!state.getValue(GameContent.CONNECTED)) return

        state.getValue(AXIS).directions.forEach { dir ->
            val otherPos = pos.relative(dir)
            val otherState = worldIn.getBlockState(otherPos)
            if (PropagatingTools.isPropagateBase(otherState)) {
                //uh lets hope all bases have facing values??
                val baseDir = worldIn.getBlockState(otherPos).getValue(BlockStateProperties.FACING)
                if (baseDir == dir.opposite) return@forEach // we don't want to be mad at the unassembled bearing

                for (it in Direction.entries) {
                    if (it == dir.opposite) continue
                    if (it == baseDir) continue // we don't want to be mad at the unassembled bearing

                    val possiblePropagator = worldIn.getBlockState(otherPos.relative(it))
                    if (PropagatingTools.isConnectedPropagator(possiblePropagator)) {
                        worldIn.destroyBlock(pos, true)
                        return
                    }
                }
            }
        }
    }

    @NoOptimize
    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        val direction = Direction.fromDelta(fromPos.x - pos.x, fromPos.y - pos.y, fromPos.z - pos.z)
        val connected = PropagatingTools.checkIfConnected(level, state, pos, direction)
        if (connected != state.getValue(GameContent.CONNECTED)) {
            level.setBlockAndUpdate(pos, state.setValue(GameContent.CONNECTED, connected))
        }
    }
}
