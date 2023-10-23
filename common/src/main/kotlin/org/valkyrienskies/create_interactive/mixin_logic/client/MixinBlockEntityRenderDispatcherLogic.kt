package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import com.simibubi.create.content.trains.bogey.StandardBogeyBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import org.joml.Vector3i
import org.joml.Vector3ic
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.shipIdToContraptionEntityClient
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.yRange

internal object MixinBlockEntityRenderDispatcherLogic {
    internal fun <E : BlockEntity?> preRender(
        blockEntity: E,
        ci: CallbackInfo
    ) {
        val level = blockEntity!!.level
        val pos = blockEntity!!.blockPos
        val ship = level.getShipManagingPos(pos) ?: return
        val contraptionEntityWeakReference =
            shipIdToContraptionEntityClient[ship.id] ?: return
        val contraptionEntity = contraptionEntityWeakReference.get() ?: return

        // Anchor at ship center
        val shipCenter: Vector3ic = ship.chunkClaim.getCenterBlockCoordinates(level!!.yRange, Vector3i())
        val relativePos = pos.subtract(shipCenter.toBlockPos())
        if (blockEntity is StandardBogeyBlockEntity || (contraptionEntity.contraption as ContraptionDuck).`ci$hasActorAtPos`(
                relativePos,
                blockEntity is MechanicalBearingBlockEntity
            )
        ) {
            // Cancel the rendering
            ci.cancel()
        }
    }
}
