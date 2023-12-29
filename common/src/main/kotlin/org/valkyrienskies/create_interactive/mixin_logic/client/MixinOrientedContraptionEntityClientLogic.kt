package org.valkyrienskies.create_interactive.mixin_logic.client

import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.contraptions.OrientedContraptionEntity
import org.joml.Vector3d
import org.joml.Vector3dc
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.create_interactive.mixin_logic.MixinOrientedContraptionEntityLogic

internal object MixinOrientedContraptionEntityClientLogic {
    internal fun preApplyLocalTransforms(contraptionEntity: OrientedContraptionEntity, matrixStack: PoseStack, ci: CallbackInfo) {
        val ship = MixinOrientedContraptionEntityLogic.getControllingShip(contraptionEntity) ?: return
        ship as ClientShip

        val rotationTransform = ship.renderTransform.shipToWorldRotation
        val angles: Vector3dc = rotationTransform.getEulerAnglesXYZ(Vector3d())
        matrixStack.translate(-.5, 0.0, -.5)

        TransformStack.cast(matrixStack)
            .nudge(contraptionEntity.id)
            .centre()
            .rotateX(Math.toDegrees(angles.x()))
            .rotateY(Math.toDegrees(angles.y()))
            .rotateZ(Math.toDegrees(angles.z()))
            .scale(ship.renderTransform.shipToWorldScaling.x().toFloat())
            // .rotateY(180.0)
            .unCentre()

        ci.cancel()
    }
}
