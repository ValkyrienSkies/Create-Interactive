package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.core.BlockPos
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.internal.joints.VSDistanceJoint
import org.valkyrienskies.core.internal.joints.VSJointId
import org.valkyrienskies.core.internal.joints.VSJointMaxForceTorque
import org.valkyrienskies.core.internal.joints.VSJointPose
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.mixin.CarriageBogeyAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD

internal object MixinCarriageContraptionEntityLogic {
    private fun Vector3d.add(vec3i: Vector3ic): Vector3d {
        return add(vec3i.x().toDouble(), vec3i.y().toDouble(), vec3i.z().toDouble())
    }

    internal fun preTick(carriageEntity: CarriageContraptionEntity){
        val level = carriageEntity.level()
        if (level.isClientSide) {
            return
        }
        val carriageIndex = carriageEntity.carriageIndex
        val shadowShipIdCopy: Long = (carriageEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return
        val ship: Ship = level.shipObjectWorld.loadedShips.getById(shadowShipIdCopy) ?: return
        val trainCarriages: List<Carriage> = carriageEntity.carriage.train.carriages
        if (carriageIndex + 1 < trainCarriages.size) {
            val frontCar = trainCarriages[carriageIndex + 1]
            val frontCarEntity = frontCar.anyAvailableEntity() ?: return
            if (frontCarEntity.level() === level) {
                val frontCarShipId = (frontCarEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`()
                if (frontCarShipId != null) {
                    val frontCarShip: Ship? = level.shipObjectWorld.loadedShips.getById(frontCarShipId)
                    if (frontCarShip != null) {
                        val shipCenterPos = ship.getChunkClaimCenterPos(level)
                        val frontShipCenterPos = frontCarShip.getChunkClaimCenterPos(level)

                        val backCarBogey = carriageEntity.carriage.bogeys.second ?: carriageEntity.carriage.bogeys.first ?: return
                        val backCarBogeyPos =
                            if ((backCarBogey as CarriageBogeyAccessor).getIsLeading()) BlockPos.ZERO else BlockPos.ZERO.relative(
                                carriageEntity.initialOrientation.counterClockWise, carriageEntity.carriage.bogeySpacing
                            )

                        val frontCarBogey = frontCarEntity.carriage.bogeys.first ?: return
                        val frontCarBogeyPos =
                            if ((frontCarBogey as CarriageBogeyAccessor).getIsLeading()) BlockPos.ZERO else BlockPos.ZERO.relative(
                                frontCarEntity.initialOrientation.counterClockWise, frontCarEntity.carriage.bogeySpacing
                            )

                        // Add an offset to the constraint positions to account for the constraints being 1 block away from the bogey
                        val carHookOffset = frontCarEntity.initialOrientation.counterClockWise.normal.toJOMLD()

                        val shipConstraintPos: Vector3dc = backCarBogeyPos.toJOMLD().add(shipCenterPos).add(0.5, 0.5, 0.5).add(carHookOffset)
                        val frontShipConstraintPos: Vector3dc = frontCarBogeyPos.toJOMLD().add(frontShipCenterPos).add(0.5, 0.5, 0.5).sub(carHookOffset)

                        // Subtract 2 to account for bogey car hooks
                        val fixedDistance: Float = (carriageEntity.carriage.train.carriageSpacing[carriageIndex] - 2).toFloat()
                        val attachmentConstraint = VSDistanceJoint(
                            ship.id,
                            VSJointPose(shipConstraintPos, Quaterniond()),
                            frontCarShip.id,
                            VSJointPose(frontShipConstraintPos, Quaterniond()),
                            VSJointMaxForceTorque(1.0E100F, 1.0E-10F),
                            1e-10,
                            fixedDistance,
                            fixedDistance
                        )
                        return ValkyrienSkiesMod.getOrCreateGTPA(level.dimensionId).addJoint(attachmentConstraint) {
                            t -> carriageEntity.jointId = t
                        }
                    }
                }
            }
        }
        return
    }

    val jointMap = mutableMapOf<CarriageContraptionEntity, VSJointId?>()

    var CarriageContraptionEntity.jointId : VSJointId?
        set(value) {
            jointMap[this] = value
        }
        get() {
            return jointMap[this]
        }
}

