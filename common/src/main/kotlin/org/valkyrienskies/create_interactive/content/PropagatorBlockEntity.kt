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
        return BlockPos(0, -60, 0)
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
            return 1.0f
        }
        return 0.0f
    }

    @NoOptimize
    override fun attachKinetics() {
        super.attachKinetics()
        //TODO attach to contraption when possible
    }

    @NoOptimize
    override fun detachKinetics() {
        super.detachKinetics()
        //TODO
    }
}
