package org.valkyrienskies.create_interactive.content.interact_me

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class InteractMeBlock(properties: Properties) : DirectionalBlock(properties.noOcclusion().instabreak()) {

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
        super.createBlockStateDefinition(builder)
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return (defaultBlockState().setValue(FACING, blockPlaceContext.clickedFace))
    }

    override fun rotate(blockState: BlockState, rotation: Rotation): BlockState {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)))
    }

    override fun mirror(blockState: BlockState, mirror: Mirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)))
    }

    override fun canSurvive(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean {
        return levelReader.getBlockState(blockPos.relative((blockState.getValue(FACING)).opposite)).isSolid
    }

    override fun updateShape(
            blockState: BlockState,
            direction: Direction,
            blockState2: BlockState?,
            levelAccessor: LevelAccessor?,
            blockPos: BlockPos?,
            blockPos2: BlockPos?,
    ): BlockState {
        return if (direction.opposite == blockState.getValue(FACING) && !blockState.canSurvive(
                levelAccessor,
                blockPos)
        ) Blocks.AIR.defaultBlockState() else super.updateShape(blockState,
            direction,
            blockState2,
            levelAccessor,
            blockPos,
            blockPos2)
    }

    override fun getShape(blockState: BlockState,
            blockGetter: BlockGetter?,
            blockPos: BlockPos?,
            collisionContext: CollisionContext?): VoxelShape? {
        return AABBS[blockState.getValue(FACING)]
    }

    companion object {
        val AABBS = Maps.newEnumMap<Direction?,VoxelShape?>(ImmutableMap.of(
            Direction.NORTH, Block.box(2.0, 2.0, 15.0, 14.0, 14.0, 16.0),
            Direction.SOUTH, Block.box(2.0, 2.0, 0.0, 14.0, 14.0, 1.0),
            Direction.EAST, Block.box(0.0, 2.0, 2.0, 1.0, 14.0, 14.0),
            Direction.WEST, Block.box(15.0, 2.0, 2.0, 16.0, 14.0, 14.0),
            Direction.UP, Block.box(2.0, 0.0, 2.0, 14.0, 1.0, 14.0),
            Direction.DOWN, Block.box(2.0, 15.0, 2.0, 14.0, 16.0, 14.0)
        ))
    }
}