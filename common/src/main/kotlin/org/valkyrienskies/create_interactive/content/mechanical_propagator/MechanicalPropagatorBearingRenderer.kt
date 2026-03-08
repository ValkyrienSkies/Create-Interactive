package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.contraptions.bearing.BearingBlock
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import dev.engine_room.flywheel.api.visualization.VisualizationManager
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.render.CachedBuffers
import net.createmod.catnip.render.SuperByteBuffer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.create_interactive.CreateInteractivePartialModels
import org.valkyrienskies.create_interactive.services.NoOptimize

class MechanicalPropagatorBearingRenderer<T>(context: BlockEntityRendererProvider.Context?) :
    KineticBlockEntityRenderer<T>(context) where T : KineticBlockEntity?, T : IBearingBlockEntity? {
    @NoOptimize
    override fun renderSafe(
        be: T, partialTicks: Float, ms: PoseStack, buffer: MultiBufferSource,
        light: Int, overlay: Int
    ) {
        if (VisualizationManager.supportsVisualization(be!!.level)) return
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay)

        val facing = be.blockState.getValue(BlockStateProperties.FACING)
        val top = CreateInteractivePartialModels.BEARING_TOP_PROPAGATOR
        val shaft = AllPartialModels.SHAFT_HALF
        val superBuffer = CachedBuffers.partial(top, be.blockState)
        val shaftBuffer = CachedBuffers.partial(shaft, be.blockState)
        val topShaftBuffer = CachedBuffers.partial(shaft, be.blockState)
        val interpolatedAngle = be.getInterpolatedAngle(partialTicks - 1)

        kineticRotationTransform(superBuffer, be, facing.axis, (interpolatedAngle / 180 * Math.PI).toFloat(), light)

        if (facing.axis.isHorizontal) superBuffer.rotateCentered(
            AngleHelper.rad(AngleHelper.horizontalAngle(facing.opposite).toDouble()),
            Direction.UP
        )
        superBuffer.rotateCentered(
            AngleHelper.rad((-90 - AngleHelper.verticalAngle(facing)).toDouble()),
            Direction.EAST
        )
        shaftBuffer.rotateCentered(
            AngleHelper.rad((-90 - AngleHelper.verticalAngle(facing)).toDouble()),
            Direction.EAST
        )
        topShaftBuffer.rotateCentered(
            AngleHelper.rad((-90 - AngleHelper.verticalAngle(facing)).toDouble()),
            Direction.EAST
        )
        superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()))
        shaftBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()))
        topShaftBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()))

    }

    @NoOptimize
    override fun getRotatedModel(be: T, state: BlockState): SuperByteBuffer? {
        return CachedBuffers.partialFacing(
            AllPartialModels.SHAFT_HALF, state, state
                .getValue(BearingBlock.FACING)
                .opposite
        )
    }
}
