package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import org.joml.Quaterniond

object MixinCBCContraptionRotationStateLogic {
    fun getRotationQuaternion(rotState: AbstractContraptionEntity.ContraptionRotationState, flag: Boolean, yawAdjust: Float, pitch: Float, dest: Quaterniond): Quaterniond {
        val newRot = dest.set(0.0, 0.0, 0.0, 1.0)

        if (rotState.hasVerticalRotation()) {
            newRot.rotateY(Math.toRadians(yawAdjust.toDouble()))
            if (flag) {
                newRot.rotateZ(Math.toRadians(pitch.toDouble()))
            } else {
                newRot.rotateX(Math.toRadians(pitch.toDouble()))
            }
        } else {
            newRot.rotateY(Math.toRadians(-yawAdjust.toDouble()))
        }
        newRot.rotateLocalY(Math.toRadians(rotState.yawOffset.toDouble()))
        return newRot
    }
}
