package org.valkyrienskies.create_interactive.mixin_logic.deployer

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity
import com.simibubi.create.foundation.utility.AnimationTickHolder
import com.simibubi.create.foundation.utility.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getActorAtPos

internal object MixinDeployerRendererLogic {
    internal fun preGetHandOffset(
        be: DeployerBlockEntity,
        partialTicks: Float,
        blockState: BlockState,
        cir: CallbackInfoReturnable<Vec3>
    ) {
        val actorAtPos = getActorAtPos(
            be.level!!, be.blockPos
        )
        if (actorAtPos != null) {
            val context = actorAtPos.right
            val factor: Double =
                if (context!!.contraption.stalled || context.position == null || context.data.contains("StationaryTimer")) {
                    (Mth.sin(AnimationTickHolder.getRenderTime() * .5f) * .25f + .25f).toDouble()
                } else {
                    val center = VecHelper.getCenterOf(BlockPos(context.position))
                    val distance = context.position.distanceTo(center)
                    val nextDistance = context.position.add(context.motion)
                        .distanceTo(center)
                    .5f - Mth.clamp(
                        Mth.lerp(AnimationTickHolder.getPartialTicks().toDouble(), distance, nextDistance),
                        0.0,
                        1.0
                    )
                }
            val offset = Vec3.atLowerCornerOf(
                blockState.getValue(DirectionalKineticBlock.FACING)
                    .normal
            ).scale(factor)
            cir.setReturnValue(offset)
        }
    }
}
