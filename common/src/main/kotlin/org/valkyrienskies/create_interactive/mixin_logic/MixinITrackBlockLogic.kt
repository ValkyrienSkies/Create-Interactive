package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.track.ITrackBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinITrackBlockLogic {
    internal fun modifyGetNearestTrackAxis(
        instance: ITrackBlock,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        blockState: BlockState,
    ): List<Vec3> {
        val original = instance.getTrackAxes(blockGetter, blockPos, blockState)
        if (blockGetter !is Level) return original
        val ship = blockGetter.getShipManagingPos(blockPos) ?: return original
        val transform = if (ship is ClientShip) ship.renderTransform else ship.transform
        return original.map { it.toJOML().rotate(transform.shipToWorldRotation).toMinecraft() }
    }
}
