package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.api.instance.DynamicInstance
import com.jozufozu.flywheel.core.materials.oriented.OrientedData
import com.mojang.math.Axis
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity
import com.simibubi.create.content.kinetics.base.BackHalfShaftInstance
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.foundation.utility.AngleHelper
import com.simibubi.create.foundation.utility.AnimationTickHolder
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.joml.Quaternionf
import org.valkyrienskies.create_interactive.CreateInteractivePartialModels
import org.valkyrienskies.create_interactive.services.NoOptimize

class MechPropBearingInstance<B>(materialManager: MaterialManager?, blockEntity: B) :
    BackHalfShaftInstance<B>(materialManager, blockEntity),
    DynamicInstance where B : KineticBlockEntity?, B : IBearingBlockEntity? {
    private val topInstance: OrientedData
    private val rotationAxis: Axis
    private val blockOrientation: Quaternionf

    init {
        val facing = blockState.getValue(BlockStateProperties.FACING)
        rotationAxis = Axis.of(Direction.get(Direction.AxisDirection.POSITIVE, axis).step())
        blockOrientation = getBlockStateOrientation(facing)
        val top = CreateInteractivePartialModels.BEARING_TOP_PROPAGATOR
        topInstance = orientedMaterial.getModel(top, blockState).createInstance()
        topInstance.setPosition(instancePosition).setRotation(blockOrientation)
    }

    @NoOptimize
    override fun beginFrame() {
        val interpolatedAngle = blockEntity!!.getInterpolatedAngle(AnimationTickHolder.getPartialTicks() - 1)
        val rot: Quaternionf = rotationAxis.rotationDegrees(interpolatedAngle)

        rot.mul(blockOrientation)

        topInstance.setRotation(rot)
    }

    @NoOptimize
    override fun updateLight() {
        super.updateLight()
        relight(pos, topInstance)
    }

    @NoOptimize
    override fun remove() {
        super.remove()
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
