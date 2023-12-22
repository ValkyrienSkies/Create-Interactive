package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.foundation.collision.Matrix3d
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.valkyrienskies.create_interactive.mixin.Matrix3dAccessor
import org.valkyrienskies.create_interactive.mixinducks.ContraptionRotationStateDuck
import org.valkyrienskies.create_interactive.services.NoOptimize

internal class CreateInteractiveContraptionRotationState(private val rotation: Quaterniondc) :
    AbstractContraptionEntity.ContraptionRotationState(),
    ContraptionRotationStateDuck {
    private var cached: Matrix3d? = null

    @NoOptimize
    override fun asMatrix(): Matrix3d = asMatrixInternal()

    // Make an internal function so proguard can obfuscate this
    private fun asMatrixInternal(): Matrix3d {
        if (cached == null) {
            cached = Matrix3d().apply {
                val jomlMatrix = org.joml.Matrix3d().rotate(rotation)

                this as Matrix3dAccessor

                setM00(jomlMatrix.m00)
                setM01(jomlMatrix.m01)
                setM02(jomlMatrix.m02)

                setM10(jomlMatrix.m10)
                setM11(jomlMatrix.m11)
                setM12(jomlMatrix.m12)

                setM20(jomlMatrix.m20)
                setM21(jomlMatrix.m21)
                setM22(jomlMatrix.m22)
            }
        }
        return cached!!
    }

    override fun `ci$getRotationQuaternion`(dest: Quaterniond): Quaterniond {
        return dest.set(rotation)
    }
}
