package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.track.ITrackBlock
import net.createmod.catnip.data.Iterate
import net.createmod.catnip.data.Pair
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinITrackBlockLogic {
    /**
     * TODO: Ideally this should only rotate if the player isn't in ship space, but this function doesn't pass in a
     *       player position so for now just assume that we should always rotate
     */
    internal fun overwriteGetNearestTrackAxis(
        trackBlock: ITrackBlock,
        world: BlockGetter,
        pos: BlockPos,
        state: BlockState,
        lookVec: Vec3,
    ): Pair<Vec3, Direction.AxisDirection> {
        val ship = (world as? Level).getShipManagingPos(pos) ?: return trackBlock.originalGetNearestTrackAxis(world, pos, state, lookVec)
        val transform = if (ship is ClientShip) ship.renderTransform else ship.transform
        val lookVecInLocal = transform.shipToWorldRotation.transformInverse(lookVec.toJOML()).toMinecraft()
        return trackBlock.originalGetNearestTrackAxis(world, pos, state, lookVecInLocal)
    }

    private fun ITrackBlock.originalGetNearestTrackAxis(
        world: BlockGetter,
        pos: BlockPos,
        state: BlockState,
        lookVec: Vec3,
    ): Pair<Vec3, Direction.AxisDirection> {
        var best: Vec3? = null
        var bestDiff = Double.MAX_VALUE
        for (vec3 in getTrackAxes(world, pos, state)) {
            for (opposite in Iterate.positiveAndNegative) {
                val distanceTo = vec3.normalize()
                    .distanceTo(lookVec.scale(opposite.toDouble()))
                if (distanceTo > bestDiff) continue
                bestDiff = distanceTo
                best = vec3
            }
        }
        return Pair.of(
            best, if (lookVec.dot(best!!.multiply(1.0, 0.0, 1.0).normalize()) < 0) Direction.AxisDirection.POSITIVE else Direction.AxisDirection.NEGATIVE
        )
    }
}
