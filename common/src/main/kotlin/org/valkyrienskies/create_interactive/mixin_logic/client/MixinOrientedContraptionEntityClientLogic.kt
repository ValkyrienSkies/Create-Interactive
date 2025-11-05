package org.valkyrienskies.create_interactive.mixin_logic.client

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.contraptions.OrientedContraptionEntity
import dev.engine_room.flywheel.lib.transform.TransformStack
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

        TransformStack.of(matrixStack)
            .nudge(contraptionEntity.id)
            .center()
            .rotateX(Math.toDegrees(angles.x()).toFloat())
            .rotateY(Math.toDegrees(angles.y()).toFloat())
            .rotateZ(Math.toDegrees(angles.z()).toFloat())
            .scale(ship.renderTransform.shipToWorldScaling.x().toFloat())
            // .rotateY(180.0)
            .uncenter()

        ci.cancel()
    }
}
