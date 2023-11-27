package org.valkyrienskies.create_interactive.content

import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.create_interactive.services.NoOptimize

class PropagatorBlockEntity(type: BlockEntityType<out PropagatorBlockEntity>, pos: BlockPos, state: BlockState) :
    PropagatingAxisBlockEntity(type, pos, state) {

    @NoOptimize
    override fun isNoisy(): Boolean = true

    fun getOtherConnection(): BlockPos? {
        if (level!!.isClientSide) return null

        if (id == -1) return null
        val propegators = this.contraption!!.getPropegators()

        if (propegators.size <= id) {
            for (i in propegators.size until id) {
                propegators.add(null)
            }
        }

        val (pos1, pos2) = propegators[id] ?: run {
            propegators[id] = Pair(worldPosition, null)
            return null
        }

        if (pos1 == null || pos2 == null) {
            return if (pos1 == null && pos2 != worldPosition) {
                propegators[id] = Pair(pos2, worldPosition)

                pos2
            } else if (pos2 == null && pos1 != worldPosition) {
                propegators[id] = Pair(pos1, worldPosition)

                pos1
            } else throw IllegalStateException("We have a valid id and contraption, but we got a empty pair?")
        }

        if (pos1 == worldPosition) return pos2
        if (pos2 == worldPosition) return pos1

        throw IllegalStateException("We have a valid id and contraption, but we are the wrong blockpos?")
    }

    // Custom Propagation
    @NoOptimize
    override fun addPropagationLocations(
        block: IRotate,
        state: BlockState,
        neighbours: MutableList<BlockPos>
    ): MutableList<BlockPos> {
        val locations = super.addPropagationLocations(block, state, neighbours)

        val otherConnection = getOtherConnection()
        if (otherConnection != null) {
            locations.add(getOtherConnection())
        }

        return locations
    }

    @NoOptimize
    override fun propagateRotationTo(
        target: KineticBlockEntity, stateFrom: BlockState, stateTo: BlockState, diff: BlockPos,
        connectedViaAxes: Boolean, connectedViaCogs: Boolean
    ): Float {
        if (target.blockPos == getOtherConnection()) {
            return 1.0f
        }
        return 0.0f
    }
}
