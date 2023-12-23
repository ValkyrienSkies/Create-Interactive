package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.jozufozu.flywheel.api.MaterialManager
import com.jozufozu.flywheel.api.instance.DynamicInstance
import com.jozufozu.flywheel.core.materials.oriented.OrientedData
import com.mojang.math.Quaternion
import com.mojang.math.Vector3f
import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity
import com.simibubi.create.content.kinetics.base.BackHalfShaftInstance
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.foundation.utility.AngleHelper
import com.simibubi.create.foundation.utility.AnimationTickHolder
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.create_interactive.CreateInteractivePartialModels

class MechPropBearingInstance<B>(materialManager: MaterialManager?, blockEntity: B) :
    BackHalfShaftInstance<B>(materialManager, blockEntity),
    DynamicInstance where B : KineticBlockEntity?, B : IBearingBlockEntity? {
    private val topInstance: OrientedData
    private val rotationAxis: Vector3f
    private val blockOrientation: Quaternion

    init {
        val facing = blockState.getValue(BlockStateProperties.FACING)
        rotationAxis = Direction.get(Direction.AxisDirection.POSITIVE, axis).step()
        blockOrientation = getBlockStateOrientation(facing)
        val top = CreateInteractivePartialModels.BEARING_TOP_PROPAGATOR
        topInstance = orientedMaterial.getModel(top, blockState).createInstance()
        topInstance.setPosition(instancePosition).setRotation(blockOrientation)
    }

    override fun beginFrame() {
        val interpolatedAngle = blockEntity!!.getInterpolatedAngle(AnimationTickHolder.getPartialTicks() - 1)
        val rot = rotationAxis.rotationDegrees(interpolatedAngle)
        rot.mul(blockOrientation)
        topInstance.setRotation(rot)
    }

    override fun updateLight() {
        super.updateLight()
        relight(pos, topInstance)
    }

    override fun remove() {
        super.remove()
        topInstance.delete()
    }

    companion object {
        fun getBlockStateOrientation(facing: Direction): Quaternion {
            val orientation: Quaternion = if (facing.axis.isHorizontal) {
                Vector3f.YP.rotationDegrees(AngleHelper.horizontalAngle(facing.opposite))
            } else {
                Quaternion.ONE.copy()
            }
            orientation.mul(Vector3f.XP.rotationDegrees(-90 - AngleHelper.verticalAngle(facing)))
            return orientation
        }
    }
}
