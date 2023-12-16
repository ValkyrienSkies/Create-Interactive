package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.foundation.collision.Matrix3d
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.valkyrienskies.create_interactive.mixin.Matrix3dAccessor
import org.valkyrienskies.create_interactive.mixinducks.ContraptionRotationStateDuck

class CreateInteractiveContraptionRotationState(private val rotation: Quaterniondc) :
    AbstractContraptionEntity.ContraptionRotationState(),
    ContraptionRotationStateDuck {
    private var cached: Matrix3d? = null
    override fun asMatrix(): Matrix3d {
        if (cached == null) {
            val jomlMatrix = org.joml.Matrix3d().rotate(rotation)
            cached = Matrix3d()
            val accessor = cached as Matrix3dAccessor

            accessor.setM00(jomlMatrix.m00)
            accessor.setM01(jomlMatrix.m01)
            accessor.setM02(jomlMatrix.m02)

            accessor.setM10(jomlMatrix.m10)
            accessor.setM11(jomlMatrix.m11)
            accessor.setM12(jomlMatrix.m12)

            accessor.setM20(jomlMatrix.m20)
            accessor.setM21(jomlMatrix.m21)
            accessor.setM22(jomlMatrix.m22)
        }
        return cached!!
    }

    override fun `ci$getRotationQuaternion`(dest: Quaterniond): Quaterniond {
        return dest.set(rotation)
    }
}
