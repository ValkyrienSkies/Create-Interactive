package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.track.ITrackBlock
import com.simibubi.create.foundation.utility.Pair
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinITrackBlockLogic {
    private val doNotRecurse = ThreadLocal.withInitial { false }

    /**
     * TODO: Ideally this should only rotate if the player isn't in ship space, but this function doesn't pass in a
     *       player position so for now just assume that we should always rotate
     */
    internal fun preGetNearestTrackAxis(
        trackBlock: ITrackBlock,
        world: BlockGetter,
        pos: BlockPos,
        state: BlockState,
        lookVec: Vec3,
        cir: CallbackInfoReturnable<Pair<Vec3, Direction.AxisDirection>>,
    ) {
        if (doNotRecurse.get()) {
            return
        }
        val ship = (world as? Level).getShipManagingPos(pos) ?: return
        val transform = if (ship is ClientShip) ship.renderTransform else ship.transform
        val lookVecInLocal = transform.shipToWorldRotation.transformInverse(lookVec.toJOML()).toMinecraft()
        doNotRecurse.set(true)
        try {
            cir.returnValue = trackBlock.getNearestTrackAxis(world, pos, state, lookVecInLocal)
        } finally {
            doNotRecurse.set(false)
        }
    }
}
