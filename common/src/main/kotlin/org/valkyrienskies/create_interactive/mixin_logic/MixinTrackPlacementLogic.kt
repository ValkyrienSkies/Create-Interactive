package org.valkyrienskies.create_interactive.mixin_logic

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.simibubi.create.content.trains.track.ITrackBlock
import net.createmod.catnip.data.Pair
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
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
        operation: Operation<Vec3>,
        level: Level,
        pos2: BlockPos,
    ): Vec3? {
        val original = operation.call(instance)
        val ship = level.getShipManagingPos(pos2) ?: return original
        val transform = if (ship is ClientShip) ship.renderTransform else ship.transform
        return transform.shipToWorldRotation.transformInverse(original.toJOML()).toMinecraft()
    }

    internal fun redirectTryConnectInvokeGetNearestTrackAxis(
        iTrackBlock: ITrackBlock,
        world: BlockGetter,
        pos: BlockPos,
        state: BlockState,
        operation: Operation<Pair<Vec3, Direction.AxisDirection>>,
        player: Player,
    ): Pair<Vec3, Direction.AxisDirection> {
        return operation.call(iTrackBlock, world, pos, state, player.lookAngle)
    }
}
