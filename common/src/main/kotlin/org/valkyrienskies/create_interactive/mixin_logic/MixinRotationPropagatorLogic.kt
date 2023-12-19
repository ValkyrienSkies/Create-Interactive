package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.ControlledContraptionEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import net.minecraft.core.BlockPos
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionEntityForShip
import org.valkyrienskies.create_interactive.content.mechanical_propagator.MechanicalPropagatorBearingBlockEntity
import org.valkyrienskies.create_interactive.mixin.contraptins.ControlledContraptionEntityAccessor
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toBlockPos

internal object MixinRotationPropagatorLogic {
    internal fun postGetPotentialNeighbourLocations(
        be: KineticBlockEntity,
        cir: CallbackInfoReturnable<MutableList<BlockPos>>
    ) {
        val ship = be.level.getShipManagingPos(be.blockPos) ?: return
        val contraptionEntity =
            getContraptionEntityForShip(ship.id, be.level!!.isClientSide) as? ControlledContraptionEntity
                ?: return
        val centerPos = ship.getChunkClaimCenterPos(be.level!!).toBlockPos()
        if (be.blockPos != centerPos) return

        // Get the pos of the contraption bearing
        val controller = (contraptionEntity as ControlledContraptionEntityAccessor).invokeGetController()
            ?: return

        // Add the pos if its tile is a MechanicalPropagatorBearingBlockEntity
        if (be.level!!.getBlockEntity(controller.blockPosition) !is MechanicalPropagatorBearingBlockEntity) return
        cir.returnValue.add(controller.blockPosition)
    }
}
