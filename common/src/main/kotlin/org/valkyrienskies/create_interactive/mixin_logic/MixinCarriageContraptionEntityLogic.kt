package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.apigame.constraints.VSAttachmentConstraint
import org.valkyrienskies.core.apigame.constraints.VSConstraintId
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.mixin.CarriageBogeyAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD

internal object MixinCarriageContraptionEntityLogic {
    private fun Vector3d.add(vec3i: Vector3ic): Vector3d {
        return add(vec3i.x().toDouble(), vec3i.y().toDouble(), vec3i.z().toDouble())
    }

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
            val frontCarEntity = frontCar.anyAvailableEntity() ?: return null
            if (frontCarEntity.level === level) {
                val frontCarShipId = (frontCarEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`()
                if (frontCarShipId != null) {
                    val frontCarShip: Ship? = level.shipObjectWorld.loadedShips.getById(frontCarShipId)
                    if (frontCarShip != null) {
                        val shipCenterPos = ship.getChunkClaimCenterPos(level)
                        val frontShipCenterPos = frontCarShip.getChunkClaimCenterPos(level)

                        val backCarBogey = carriageEntity.carriage.bogeys.second ?: carriageEntity.carriage.bogeys.first ?: return null
                        val backCarBogeyPos =
                            if ((backCarBogey as CarriageBogeyAccessor).getIsLeading()) BlockPos.ZERO else BlockPos.ZERO.relative(
                                carriageEntity.initialOrientation.counterClockWise, carriageEntity.carriage.bogeySpacing
                            )

                        val frontCarBogey = frontCarEntity.carriage.bogeys.first ?: return null
                        val frontCarBogeyPos =
                            if ((frontCarBogey as CarriageBogeyAccessor).getIsLeading()) BlockPos.ZERO else BlockPos.ZERO.relative(
                                frontCarEntity.initialOrientation.counterClockWise, frontCarEntity.carriage.bogeySpacing
                            )

                        // Add an offset to the constraint positions to account for the constraints being 1 block away from the bogey
                        val carHookOffset = frontCarEntity.initialOrientation.counterClockWise.normal.toJOMLD()

                        val shipConstraintPos: Vector3dc = backCarBogeyPos.toJOMLD().add(shipCenterPos).add(0.5, 0.5, 0.5).add(carHookOffset)
                        val frontShipConstraintPos: Vector3dc = frontCarBogeyPos.toJOMLD().add(frontShipCenterPos).add(0.5, 0.5, 0.5).sub(carHookOffset)

                        // Subtract 2 to account for bogey car hooks
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
