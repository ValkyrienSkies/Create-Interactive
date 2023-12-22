package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.OrientedContraptionEntity
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.world.phys.Vec3
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

    internal fun getControllingShip(contraptionEntity: OrientedContraptionEntity): Ship? {
        val shipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return null
        val ship = contraptionEntity.level.shipObjectWorld.allShips.getById(shipId) ?: return null
        if (contraptionEntity is CarriageContraptionEntity && CreateInteractiveUtil.isTrainDerailed(contraptionEntity)) {
            return ship
        }
        return null
    }
}
