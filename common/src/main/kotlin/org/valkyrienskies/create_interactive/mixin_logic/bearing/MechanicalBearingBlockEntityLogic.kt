package org.valkyrienskies.create_interactive.mixin_logic.bearing

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import net.createmod.catnip.animation.AnimationTickHolder
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.mixin.bearing.StabilizedBearingMovementBehaviourAccessor

object MechanicalBearingBlockEntityLogic {
    internal fun preGetInterpolatedAngle(
        be: MechanicalBearingBlockEntity, cir: CallbackInfoReturnable<Float>
    ) {
        val actorAtPos = CreateInteractiveUtil.getActorAtPos(be.level!!, be.blockPos)
        if (actorAtPos != null) {
            val context = actorAtPos.right!!
            val blockState: BlockState = context.state
            val facing = blockState.getValue(BlockStateProperties.FACING)
            val rotAngle = StabilizedBearingMovementBehaviourAccessor.invokeGetCounterRotationAngle(
                context,
                facing,
                AnimationTickHolder.getPartialTicks(),
            )
            if (rotAngle != 0.0f) {
                cir.returnValue = rotAngle
            }
        }
    }
}
