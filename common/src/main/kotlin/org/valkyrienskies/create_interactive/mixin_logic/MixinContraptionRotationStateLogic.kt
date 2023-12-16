package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import org.joml.Quaterniond

internal object MixinContraptionRotationStateLogic {
    internal fun getRotationQuaternion(rotState: AbstractContraptionEntity.ContraptionRotationState, xRot: Float, yRot: Float, zRot: Float, dest: Quaterniond): Quaterniond {
        dest.set(0.0, 0.0, 0.0, 1.0)
        val newRot = dest.rotateZYX(
            Math.toRadians(zRot.toDouble()),
            Math.toRadians(yRot.toDouble()),
            Math.toRadians(xRot.toDouble()),
        )
        newRot.rotateLocalY(Math.toRadians(rotState.yawOffset.toDouble()))
        return newRot.normalize()
    }
}
