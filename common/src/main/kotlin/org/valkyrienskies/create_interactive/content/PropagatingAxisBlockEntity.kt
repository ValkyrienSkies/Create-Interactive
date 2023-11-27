package org.valkyrienskies.create_interactive.content

import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.directions
import org.valkyrienskies.mod.common.getShipManagingPos

abstract class PropagatingAxisBlockEntity(
    type: BlockEntityType<out PropagatingAxisBlockEntity>, pos: BlockPos, state: BlockState
) : DirectionalShaftHalvesBlockEntity(type, pos, state) {
    val axis get() = blockState.getValue(BlockStateProperties.AXIS)
    val isConnected get() = blockState.getValue(GameContent.CONNECTED)

    val contraption: Contraption? get() = if(!isConnected) null else _contraption ?: findBase().apply { _contraption = this };
    val id get() = if(!isConnected) -1 else { if (_contraption == null) findBase(); _id}
    private var _contraption : Contraption? = null
    private var _id: Int = -1

    private fun findBase(): Contraption? {
        for (dir in axis.directions) {
            var length = 0
            var location = worldPosition
            do {
                val shift = location.relative(dir)
                val state = level!!.getBlockState(shift)
                location = shift
                length++

                if (PropagatingTools.isPropagateBase(state)) {
                    _id = length
                    return PropegatingTools.getContraptionOfPropegateBase(level!!.getBlockEntity(shift)!!)?.contraption
                }

                if (PropagatingTools.isContraptionBase(level!!, location)) {
                    val weakEntity = CreateInteractiveUtil.shipIdToContraptionEntityServer[level.getShipManagingPos(location)!!.id]
                        ?: throw IllegalStateException("Can't find owning contraption entity!")

                    val entity = weakEntity.get() ?: throw IllegalStateException("Owner contraption entity is no more?")

                    _id = length
                    return entity.contraption
                }

            } while(PropagatingTools.isConnectedPropagator(state))
        }

        if (isConnected)
            throw IllegalStateException("This block entity is connected to a base, but we can't find it!")
        else
            return null
    }
}
