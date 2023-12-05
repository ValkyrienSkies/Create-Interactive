package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.server.level.ServerLevel
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSConstraintId
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld

internal object MixinCarriageContraptionEntityLogic {
    internal fun preTick(carriageEntity: CarriageContraptionEntity): VSConstraintId? {
        val level = carriageEntity.level
        if (level.isClientSide) {
            return null
        }
        val carriageIndex = carriageEntity.carriageIndex
        val shadowShipIdCopy: Long = (carriageEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return null
        val ship: Ship = level.shipObjectWorld.loadedShips.getById(shadowShipIdCopy) ?: return null
        val trainCarriages: List<Carriage> = carriageEntity.carriage.train.carriages
        if (carriageIndex + 1 < trainCarriages.size) {
            val frontCar = trainCarriages[carriageIndex + 1]
            val frontCarEntity = frontCar.anyAvailableEntity()
            if (frontCarEntity.level === level) {
                val frontCarShipId = (frontCarEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`()
                if (frontCarShipId != null) {
                    val frontCarShip: Ship?= level.shipObjectWorld.loadedShips.getById(frontCarShipId)
                    if (frontCarShip != null) {
                        val shipCenterPos = ship.getChunkClaimCenterPos(level)
                        val frontShipCenterPos = frontCarShip.getChunkClaimCenterPos(level)
                        // TODO: Is this always along the x-axis???
                        // TODO: Handle other bogeys
                        val shipConstraintPos: Vector3dc = Vector3d(shipCenterPos).add(0.5, 0.5, 0.5).add(1.0, 0.0, 0.0)
                        val frontShipConstraintPos: Vector3dc =
                            Vector3d(frontShipCenterPos).add(0.5, 0.5, 0.5).sub(1.0, 0.0, 0.0)

                        // Subtract 2 to account for bogey offsets
                        val fixedDistance: Double = (carriageEntity.carriage.train.carriageSpacing[carriageIndex] - 2).toDouble()
                        val attachmentConstraint = VSAttachmentConstraint(
                            ship.id,
                            frontCarShip.id,
                            1e-10,
                            shipConstraintPos,
                            frontShipConstraintPos,
                            1e100,
                            fixedDistance
                        )
                        return (level as ServerLevel).shipObjectWorld.createNewConstraint(attachmentConstraint)
                    }
                }
            }
        }
        return null
    }
}
