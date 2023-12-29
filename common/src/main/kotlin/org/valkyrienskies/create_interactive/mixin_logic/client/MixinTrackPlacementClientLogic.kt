package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.content.trains.track.TrackBlockItem
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.mod.common.PlayerUtil.transformPlayerTemporarily
import org.valkyrienskies.mod.common.PlayerUtil.untransformPlayer

internal object MixinTrackPlacementClientLogic {
    /**
     * Fix placing tracks on rotated ships not working properly
     */
    internal fun redirectClientTickInvokeGetPlacementState(
        instance: TrackBlockItem,
        pContext: UseOnContext
    ): BlockState? {
        val player = pContext.player
        val clickedPos = pContext.clickedPos
        if (player == null || clickedPos == null) {
            return instance.getPlacementState(pContext)
        }
        transformPlayerTemporarily(player, pContext.level, clickedPos)
        try {
            return instance.getPlacementState(pContext)
        } finally {
            untransformPlayer(player)
        }
    }
}
