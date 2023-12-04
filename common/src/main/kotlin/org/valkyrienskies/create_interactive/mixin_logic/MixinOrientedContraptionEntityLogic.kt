package org.valkyrienskies.create_interactive.mixin_logic

import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.contraptions.OrientedContraptionEntity
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.joml.Vector3dc
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinOrientedContraptionEntityLogic {
    internal fun preApplyLocalTransforms(contraptionEntity: OrientedContraptionEntity, matrixStack: PoseStack, ci: CallbackInfo) {
        val ship = getControllingShip(contraptionEntity) ?: return
        ship as ClientShip

        val rotationTransform = ship.renderTransform.shipToWorldRotation
        val angles: Vector3dc = rotationTransform.getEulerAnglesXYZ(Vector3d())
        matrixStack.translate(-.5, 0.0, -.5)

        TransformStack.cast(matrixStack)
            .nudge(contraptionEntity.getId())
            .centre()
            .rotateX(Math.toDegrees(angles.x()))
            .rotateY(Math.toDegrees(angles.y()))
            .rotateZ(Math.toDegrees(angles.z()))
            // .rotateY(180.0)
            .unCentre()

        ci.cancel()
    }

    internal fun preGetViewXRot(contraptionEntity: OrientedContraptionEntity, cir: CallbackInfoReturnable<Float>) {
        if (getControllingShip(contraptionEntity) != null) {
            cir.returnValue = 0.0f
        }
    }

    internal fun preGetViewYRot(contraptionEntity: OrientedContraptionEntity, cir: CallbackInfoReturnable<Float>) {
        if (getControllingShip(contraptionEntity) != null) {
            cir.returnValue = 0.0f
        }
    }

    internal fun preApplyRotation(contraptionEntity: OrientedContraptionEntity, localPos: Vec3, partialTicks: Float, cir: CallbackInfoReturnable<Vec3>) {
        val ship = getControllingShip(contraptionEntity) ?: return
        val transform: ShipTransform = if (partialTicks != 1.0f && ship is ClientShip) {
            ship.renderTransform
        } else {
            ship.transform
        }
        cir.returnValue = transform.shipToWorldRotation.transform(localPos.toJOML()).toMinecraft()
    }

    internal fun preReverseRotation(contraptionEntity: OrientedContraptionEntity, localPos: Vec3, partialTicks: Float, cir: CallbackInfoReturnable<Vec3>) {
        val ship = getControllingShip(contraptionEntity) ?: return
        val transform: ShipTransform = if (partialTicks != 1.0f && ship is ClientShip) {
            ship.renderTransform
        } else {
            ship.transform
        }
        cir.returnValue = transform.shipToWorldRotation.transformInverse(localPos.toJOML()).toMinecraft()
    }

    private fun getControllingShip(contraptionEntity: OrientedContraptionEntity): Ship? {
        val shipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return null
        val ship = contraptionEntity.level.shipObjectWorld.allShips.getById(shipId) ?: return null
        if (contraptionEntity is CarriageContraptionEntity && CreateInteractiveUtil.isTrainDerailed(contraptionEntity)) {
            return ship
        }
        return null
    }
}
