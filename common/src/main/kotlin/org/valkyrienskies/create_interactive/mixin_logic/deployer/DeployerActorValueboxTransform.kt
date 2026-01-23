package org.valkyrienskies.create_interactive.mixin_logic.deployer

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.kinetics.deployer.DeployerBlock
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
import dev.engine_room.flywheel.lib.transform.TransformStack
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.create_interactive.services.NoOptimize

class DeployerActorValueboxTransform : ValueBoxTransform.Sided() {
    @NoOptimize
    override fun getLocalOffset(level: LevelAccessor, pos: BlockPos, state: BlockState): Vec3 {
        val facing = state.getValue(DeployerBlock.FACING)
        var vec = when (facing) {
            Direction.UP -> VecHelper.voxelSpace(8.0, 4.0, 15.5)
            Direction.DOWN -> VecHelper.voxelSpace(8.0, 12.0, 15.5)
            Direction.EAST -> VecHelper.voxelSpace(4.0, 8.0, 15.5)
            Direction.WEST -> VecHelper.voxelSpace(12.0, 8.0, 15.5)
            Direction.NORTH -> VecHelper.voxelSpace(8.0, 4.0, 15.5)
            Direction.SOUTH -> VecHelper.voxelSpace(8.0, 12.0, 15.5)
            else -> VecHelper.voxelSpace(8.0, 8.0, 15.5)
        }
        vec = VecHelper.rotateCentered(vec, AngleHelper.horizontalAngle(side).toDouble(), Direction.Axis.Y)
        vec = VecHelper.rotateCentered(vec, AngleHelper.verticalAngle(side).toDouble(), Direction.Axis.X)
        vec = vec.subtract(
            Vec3.atLowerCornerOf(facing.normal)
                .scale((2 / 16f).toDouble())
        )
        return vec
    }

    @NoOptimize
    override fun isSideActive(state: BlockState, direction: Direction): Boolean {
        val facing = state.getValue(DeployerBlock.FACING)
        if (direction.axis === facing.axis) return false
        return (state.block as DeployerBlock).getRotationAxis(state) !== direction.axis
    }

    @NoOptimize
    override fun rotate(level: LevelAccessor, pos: BlockPos, state: BlockState, ms: PoseStack) {
        val facing: Direction = side
        val xRot = (if (facing == Direction.UP) 90 else if (facing == Direction.DOWN) 270 else 0).toFloat()
        val yRot = AngleHelper.horizontalAngle(facing) + 180
        if (facing.axis === Direction.Axis.Y) TransformStack.of(ms)
            .rotateY((180 + AngleHelper.horizontalAngle(state.getValue(DeployerBlock.FACING))).toDouble().toFloat())
        TransformStack.of(ms)
            .rotateY(yRot)
            .rotateX(xRot)
    }

    @NoOptimize
    override fun getSouthLocation(): Vec3 {
        return Vec3.ZERO
    }
}