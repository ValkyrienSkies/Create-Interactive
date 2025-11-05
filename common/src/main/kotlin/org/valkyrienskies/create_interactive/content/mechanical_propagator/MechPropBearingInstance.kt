package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.mojang.math.Axis
import com.simibubi.create.content.contraptions.bearing.BearingVisual
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import dev.engine_room.flywheel.api.visual.DynamicVisual
import dev.engine_room.flywheel.api.visualization.VisualizationContext
import dev.engine_room.flywheel.lib.instance.InstanceTypes
import dev.engine_room.flywheel.lib.instance.OrientedInstance
import dev.engine_room.flywheel.lib.model.Models
import net.createmod.catnip.math.AngleHelper
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Quaternionf
import org.valkyrienskies.create_interactive.CreateInteractivePartialModels
import org.valkyrienskies.create_interactive.services.NoOptimize

class MechPropBearingInstance<B>(visualizationContext: VisualizationContext?, blockEntity: B, partialTick: Float) :
    BearingVisual<B>(visualizationContext, blockEntity, partialTick),
    DynamicVisual where B : KineticBlockEntity?, B : IBearingBlockEntity? {
    private val topInstance: OrientedInstance
    private val rotationAxis: Axis
    private val blockOrientation: Quaternionf

    init {
        val facing = blockState.getValue(BlockStateProperties.FACING)
        rotationAxis = Axis.of(Direction.get(Direction.AxisDirection.POSITIVE, facing.axis).step())
        blockOrientation = getBlockStateOrientation(facing)
        val top = CreateInteractivePartialModels.BEARING_TOP_PROPAGATOR
        topInstance = instancerProvider().instancer(InstanceTypes.ORIENTED, Models.partial(top)).createInstance()
        topInstance.position(visualPosition)
        topInstance.rotation(blockOrientation)
    }

    @NoOptimize
    override fun beginFrame(ctx: DynamicVisual.Context) {
        val interpolatedAngle = blockEntity!!.getInterpolatedAngle(ctx.partialTick() - 1)
        val rot: Quaternionf = rotationAxis.rotationDegrees(interpolatedAngle)

        rot.mul(blockOrientation)

        topInstance.rotation(rot)
    }

    @NoOptimize
    override fun updateLight(partialTick: Float) {
        super.updateLight(partialTick)
        relight(pos, topInstance)
    }

    @NoOptimize
    override fun _delete() {
        super._delete()
        topInstance.delete()
    }

    companion object {
        private fun getBlockStateOrientation(facing: Direction): Quaternionf {
            val orientation: Quaternionf = if (facing.axis.isHorizontal) {
                Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing.opposite))
            } else {
                Quaternionf()
            }
            orientation.mul(Axis.XP.rotationDegrees(-90 - AngleHelper.verticalAngle(facing)))
            return orientation
        }
    }
}
