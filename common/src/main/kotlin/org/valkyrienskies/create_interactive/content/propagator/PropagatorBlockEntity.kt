package org.valkyrienskies.create_interactive.content.propagator

import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.create_interactive.content.PropagatingTools
import org.valkyrienskies.create_interactive.content.propagating_axis.PropagatingAxisBlockEntity
import org.valkyrienskies.create_interactive.services.NoOptimize

class PropagatorBlockEntity(type: BlockEntityType<out PropagatorBlockEntity>, pos: BlockPos, state: BlockState) :
    PropagatingAxisBlockEntity(type, pos, state) {

    @NoOptimize
    override fun isNoisy(): Boolean = true

    private fun getOtherConnection(): BlockPos? {
        if (level!!.isClientSide) return null
        val contraption = this.contraption ?: return null
        if (id == -1) return null

        val propagators = PropagatingTools.getPropagators(contraption)

        if (propagators.size <= id) {
            println("RESIZING PROPAGATORS FOR $id")
            repeat(id - propagators.size + 1) {
                propagators.add(null)
            }
        }

        val (pos1, pos2) = propagators[id] ?: run {
            println("ADDED PROPAGATOR $id $worldPosition")
            propagators[id] = Pair(worldPosition, null)
            return@getOtherConnection null
        }

        if (pos1 == worldPosition) return pos2
        if (pos2 == worldPosition) return pos1

        if (pos1 == null || pos2 == null) {
            println("TRYING TO FULLFULL PROPAGATOR $id")
            //TODO if we fullfill a pair, update the other pair to notice the change

            if (pos1 == null && pos2 != worldPosition) {
                propagators[id] = Pair(pos2, worldPosition)

                return pos2
            } else if (pos2 == null && pos1 != worldPosition) {
                propagators[id] = Pair(pos1, worldPosition)

                return pos1
            } else if (pos1 == null && pos2 == null) {
                throw IllegalStateException("We have a valid id and contraption, but we got a empty pair?")
            }

            println("FULLFILLED PROPAGATOR $id")
        }

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
