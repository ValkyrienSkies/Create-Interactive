package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionEntityForShip
import org.valkyrienskies.mod.common.getShipManagingPos

internal object MixinControlsBlockLogic {
    /**
     * Fix control blocks being closed when placed on train contraptions
     */
    internal fun postGetStateForPlacement(
        pContext: BlockPlaceContext,
        cir: CallbackInfoReturnable<BlockState>,
    ) {
        handleCommonLogic(pContext.level, pContext.clickedPos, cir)
    }

    internal fun postUpdateShape(
        pLevel: LevelAccessor,
        pCurrentPos: BlockPos,
        cir: CallbackInfoReturnable<BlockState>
    ) {
        if (pLevel !is Level) return
        handleCommonLogic(pLevel, pCurrentPos, cir)
    }

    private fun handleCommonLogic(
        level: Level,
        blockPos: BlockPos,
        cir: CallbackInfoReturnable<BlockState>,
    ) {
        val ship = level.getShipManagingPos(blockPos) ?: return
        val contraptionEntity = getContraptionEntityForShip(ship.id, level.isClientSide) ?: return
        if (contraptionEntity is CarriageContraptionEntity) {
            val originalState = cir.returnValue
            val newState = originalState.setValue(ControlsBlock.OPEN, true)
            cir.setReturnValue(newState)
        }
    }
}
