package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.contraptions.bearing.BearingRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.utility.AngleHelper
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.create_interactive.CreateInteractivePartialModels
import org.valkyrienskies.create_interactive.services.NoOptimize

class DisjointedPropagatorBearingRenderer(context: BlockEntityRendererProvider.Context): BearingRenderer<DisjointedPropagatorBearingBlockEntity>(context) {
    @NoOptimize
    override fun renderSafe(
        be: DisjointedPropagatorBearingBlockEntity, partialTicks: Float, ms: PoseStack?, buffer: MultiBufferSource,
        light: Int, overlay: Int
    ) {
        val state = getRenderedBlockState(be)
        val type = getRenderType(be, state)
        if (type != null) renderRotatingBuffer(be, getRotatedModel(be, state), ms, buffer.getBuffer(type), light)

        val facing: Direction = be.blockState.getValue(BlockStateProperties.FACING)
        val top = CreateInteractivePartialModels.BEARING_TOP_PROPAGATOR
        val cog = CreateInteractivePartialModels.BEARING_COG
        val superBuffer = CachedBufferer.partial(top, be.blockState)
        val cogBuffer = CachedBufferer.partial(cog, be.blockState)
        val interpolatedAngle: Float = be.getDisjointInterpolatedAngle(partialTicks - 1)
        val cogAngle: Float = be.getInterpolatedAngle(partialTicks - 1)
        kineticRotationTransform(superBuffer, be, facing.axis, (interpolatedAngle / 180 * Math.PI).toFloat(), light)
        kineticRotationTransform(cogBuffer, be, facing.axis, (cogAngle / 180 * Math.PI).toFloat(), light)
        if (facing.axis.isHorizontal) superBuffer.rotateCentered(
            Direction.UP,
            AngleHelper.rad(AngleHelper.horizontalAngle(facing.opposite).toDouble())
        )

        if (facing.axis.isHorizontal) cogBuffer.rotateCentered(
            Direction.UP,
            AngleHelper.rad(AngleHelper.horizontalAngle(facing.opposite).toDouble())
        )

        superBuffer.rotateCentered(
            Direction.EAST,
            AngleHelper.rad((-90 - AngleHelper.verticalAngle(facing)).toDouble())
        )
        cogBuffer.rotateCentered(
            Direction.EAST,
            AngleHelper.rad((-90 - AngleHelper.verticalAngle(facing)).toDouble())
        )
        superBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()))
        cogBuffer.renderInto(ms, buffer.getBuffer(RenderType.solid()))
    }
}
