package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.create_interactive.services.NoOptimize
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toBlockPos

class MechanicalPropagatorBearingBlockEntity(
    type: BlockEntityType<out MechanicalPropagatorBearingBlockEntity>, pos: BlockPos, state: BlockState
): MechanicalBearingBlockEntity(type, pos, state) {
    private fun getOtherConnection(): BlockPos? {
        if (level!!.isClientSide) return null
        val contraption = movedContraption ?: return null
        val shipId = (contraption as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return null
        val ship: Ship = level.shipObjectWorld.allShips.getById(shipId) ?: return null
        // Anchor at ship center
        return ship.getChunkClaimCenterPos(level!!).toBlockPos()
    }

    // Custom Propagation
    @NoOptimize
    override fun addPropagationLocations(
        block: IRotate,
        state: BlockState?,
        neighbours: MutableList<BlockPos?>
    ): MutableList<BlockPos?> {
        val locations = super.addPropagationLocations(block, state, neighbours)
        if (getOtherConnection() != null) {
            locations.add(getOtherConnection())
        }
        return locations
    }

    @NoOptimize
    override fun propagateRotationTo(
        target: KineticBlockEntity, stateFrom: BlockState?, stateTo: BlockState?, diff: BlockPos?,
        connectedViaAxes: Boolean, connectedViaCogs: Boolean
    ): Float {
        if (target.blockPos == getOtherConnection()) {
            val toBlock = stateTo!!.block
            if (toBlock !is IRotate) return 0.0f
            val direction = blockState.getValue(DirectionalKineticBlock.FACING).opposite
            if (!toBlock.hasShaftTowards(target.level, target.blockPos, target.blockState, direction)) {
                return 0.0f
            }
            return 1.0f
        }
        return 0.0f
    }
}
