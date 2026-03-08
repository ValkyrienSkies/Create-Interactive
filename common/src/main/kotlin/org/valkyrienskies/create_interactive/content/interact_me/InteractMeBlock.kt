package org.valkyrienskies.create_interactive.content.interact_me

import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity
import com.simibubi.create.foundation.block.ProperWaterloggedBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class InteractMeBlock(properties: Properties) : FaceAttachedHorizontalDirectionalBlock(properties.noOcclusion().instabreak()) {

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder.add(FACE, FACING, ProperWaterloggedBlock.WATERLOGGED))
    }

    override fun rotate(blockState: BlockState, rotation: Rotation): BlockState {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)))
    }

    override fun mirror(blockState: BlockState, mirror: Mirror): BlockState {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)))
    }

    override fun getShape(blockState: BlockState,
            blockGetter: BlockGetter?,
            blockPos: BlockPos?,
            collisionContext: CollisionContext?): VoxelShape? {
        return AABBS[getConnectedDirection(blockState)]
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