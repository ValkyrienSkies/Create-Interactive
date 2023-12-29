package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.track.ITrackBlock
import com.simibubi.create.foundation.utility.Pair
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinTrackPlacementLogic {
    internal fun redirectTryConnectInvokeGetLookAngle(
        instance: Player,
        level: Level,
        player: Player,
        pos2: BlockPos,
        state2: BlockState,
        stack: ItemStack,
        girder: Boolean,
        maximiseTurn: Boolean
    ): Vec3? {
        val original = instance.lookAngle
        val ship = level.getShipManagingPos(pos2) ?: return original
        val transform = if (ship is ClientShip) ship.renderTransform else ship.transform
        return transform.shipToWorldRotation.transformInverse(original.toJOML()).toMinecraft()
    }

    internal fun redirectTryConnectInvokeGetNearestTrackAxis(
        iTrackBlock: ITrackBlock,
        world: BlockGetter,
        pos: BlockPos,
        state: BlockState,
        lookVec: Vec3,
        level: Level,
        player: Player,
        pos2: BlockPos,
        state2: BlockState,
        stack: ItemStack,
        girder: Boolean,
        maximiseTurn: Boolean
    ): Pair<Vec3, Direction.AxisDirection> {
        return iTrackBlock.getNearestTrackAxis(world, pos, state, player.lookAngle)
    }
}
