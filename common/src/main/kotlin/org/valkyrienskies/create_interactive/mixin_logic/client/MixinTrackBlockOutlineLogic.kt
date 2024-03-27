package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.content.trains.track.TrackBlockOutline
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.create_interactive.mixin.TrackBlockOutlineAccessor
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft

object MixinTrackBlockOutlineLogic {
    /**
     * Fixed train station direction wrong on ships
     */
    fun postClientTick() {
        val origResult = TrackBlockOutlineAccessor.getResult() ?: return
        val clientShip =
            origResult.blockEntity.level.getShipManagingPos(origResult.blockEntity.blockPos) as? ClientShip ?: return
        val newDirection =
            origResult.direction.toJOML().rotate(clientShip.renderTransform.shipToWorldRotation).toMinecraft()

        TrackBlockOutlineAccessor.setResult(
            TrackBlockOutline.BezierPointSelection(
                origResult.blockEntity, origResult.loc, origResult.vec, origResult.angles, newDirection
            )
        )
    }
}
