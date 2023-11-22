package org.valkyrienskies.create_interactive.content

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.ControlledContraptionEntity
import com.simibubi.create.content.contraptions.IControlContraption
import com.simibubi.create.content.contraptions.bearing.BearingBlock
import com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlock
import com.simibubi.create.content.contraptions.bearing.ClockworkBearingBlockEntity
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.piston.PistonBaseBlock
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.directions
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML

object PropegatingTools {

    fun getContraptionOfPropegateBase(be: BlockEntity): AbstractContraptionEntity? {
        return when(be) {
            is MechanicalBearingBlockEntity -> be.movedContraption
            is LinearActuatorBlockEntity -> be.movedContraption
            else -> null
        }
    }

    fun isPropegateBase(blockState: BlockState): Boolean {
        return blockState.block is BearingBlock || blockState.block is PistonBaseBlock
    }

    fun checkIfConnected(level: Level, state: BlockState, pos: BlockPos, direction: Direction?): Boolean {
        return if (direction == null) {
            state.getValue(RotatedPillarKineticBlock.AXIS).directions.any { checkIfConnected(level, state, pos, it) }
        } else {
            val blockState = level.getBlockState(pos.relative(direction))
            val isPropegating = isConnectedPropagator(blockState) &&
                    blockState.getValue(RotatedPillarKineticBlock.AXIS) == state.getValue(RotatedPillarKineticBlock.AXIS)

            isPropegating || isPropegateBase(blockState) || isContraptionBase(level, pos.relative(direction))
        }
    }

    fun isConnectedPropagator(blockState: BlockState): Boolean {
        return blockState.block is PropegatingAxisBlock && blockState.getValue(GameContent.CONNECTED)
    }

    fun isContraptionBase(level: Level, location: BlockPos): Boolean {
        return level.getShipManagingPos(location)?.getChunkClaimCenterPos(level) == location.toJOML()
    }
}