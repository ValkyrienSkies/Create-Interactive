package org.valkyrienskies.create_interactive.content

import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class PropegatorBlockEntity(type: BlockEntityType<out PropegatorBlockEntity>, pos: BlockPos, state: BlockState) :
    PropegatingAxisBlockEntity(type, pos, state) {

    override fun isNoisy(): Boolean = true

    fun getOtherConnection(): BlockPos? {
        return BlockPos(0, -60, 0)
    }

    // Custom Propagation
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

    override fun propagateRotationTo(
        target: KineticBlockEntity, stateFrom: BlockState?, stateTo: BlockState?, diff: BlockPos?,
        connectedViaAxes: Boolean, connectedViaCogs: Boolean
    ): Float {
        if (target.blockPos == getOtherConnection()) {
            return 1.0f
        }
        return 0.0f
    }

    override fun attachKinetics() {
        super.attachKinetics()
        //TODO attach to contraption when possible
    }

    override fun detachKinetics() {
        super.detachKinetics()
        //TODO
    }
}
