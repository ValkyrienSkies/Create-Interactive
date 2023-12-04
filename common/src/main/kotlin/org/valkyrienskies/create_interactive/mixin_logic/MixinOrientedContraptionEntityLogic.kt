package org.valkyrienskies.create_interactive.mixin_logic

import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.contraptions.OrientedContraptionEntity
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.client.multiplayer.ClientLevel
import org.joml.Vector3d
import org.joml.Vector3dc
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld

internal object MixinOrientedContraptionEntityLogic {
    internal fun preApplyLocalTransforms(contraptionEntity: OrientedContraptionEntity, matrixStack: PoseStack, ci: CallbackInfo) {
        val shipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return
        val clientShip = (contraptionEntity.level as ClientLevel).shipObjectWorld.allShips.getById(shipId) ?: return
        if (contraptionEntity is CarriageContraptionEntity && CreateInteractiveUtil.isTrainDerailed(contraptionEntity)) {
            val rotationTransform = clientShip.renderTransform.shipToWorldRotation

            val angles: Vector3dc = rotationTransform.getEulerAnglesZYX(Vector3d())

            matrixStack.translate(-.5, 0.0, -.5)

            TransformStack.cast(matrixStack)
                .nudge(contraptionEntity.getId())
                .centre()
                .rotateZ(Math.toDegrees(angles.z()))
                .rotateY(Math.toDegrees(angles.y()))
                .rotateX(Math.toDegrees(angles.x()))
                // .rotateY(180.0)
                .unCentre()

            ci.cancel()
        }
    }
}
