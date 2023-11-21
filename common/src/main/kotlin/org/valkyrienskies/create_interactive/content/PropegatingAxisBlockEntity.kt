package org.valkyrienskies.create_interactive.content

import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.kinetics.base.DirectionalShaftHalvesBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.directions

abstract class PropegatingAxisBlockEntity(
    type: BlockEntityType<out PropegatingAxisBlockEntity>, pos: BlockPos, state: BlockState
) : DirectionalShaftHalvesBlockEntity(type, pos, state) {
    val axis get() = blockState.getValue(BlockStateProperties.AXIS)
    val isConnected get() = blockState.getValue(GameContent.CONNECTED)

    val contraption: Contraption? get() = if(isConnected) null else _contraption ?: findBase().apply { _contraption = this };
    private var _contraption : Contraption? = null

    private fun findBase(): Contraption? {
        for (dir in axis.directions) {
            var location = worldPosition
            do {
                val shift = location.relative(dir)
                val state = level!!.getBlockState(shift)
                location = shift

                if (PropegatingTools.isPropegateBase(state))
                    return PropegatingTools.getContraptionOfPropegateBase(level!!.getBlockEntity(shift)!!)?.contraption

                if (PropegatingTools.isContraptionBase(level!!, location))
                    return TODO()

            } while(PropegatingTools.isConnectedPropagator(state))
        }

        if (isConnected)
            throw IllegalStateException("This block entity is connected to a base, but we can't find it!")
        else
            return null
    }
}