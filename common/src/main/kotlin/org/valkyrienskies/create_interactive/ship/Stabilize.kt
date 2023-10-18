package org.valkyrienskies.create_interactive.ship

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.create_interactive.CreateInteractiveConfig

fun stabilize(
    ship: PhysShipImpl,
    omega: Vector3dc,
    vel: Vector3dc,
    forces: PhysShip,
    linear: Boolean,
    yaw: Boolean
) {
    val shipUp = Vector3d(0.0, 1.0, 0.0)
    val worldUp = Vector3d(0.0, 1.0, 0.0)
    ship.poseVel.rot.transform(shipUp)

    val angleBetween = shipUp.angle(worldUp)
    val idealAngularAcceleration = Vector3d()
    if (angleBetween > .01) {
        val stabilizationRotationAxisNormalized = shipUp.cross(worldUp, Vector3d()).normalize()
        idealAngularAcceleration.add(
            stabilizationRotationAxisNormalized.mul(
                angleBetween,
                stabilizationRotationAxisNormalized
            )
        )
    }

    // Only subtract the x/z components of omega.
    // We still want to allow rotation along the Y-axis (yaw).
    // Except if yaw is true, then we stabilize
    idealAngularAcceleration.sub(
        omega.x(),
        if (!yaw) 0.0 else omega.y(),
        omega.z()
    )

    val stabilizationTorque = ship.poseVel.rot.transform(
        ship.inertia.momentOfInertiaTensor.transform(
            ship.poseVel.rot.transformInverse(idealAngularAcceleration)
        )
    )

    stabilizationTorque.mul(CreateInteractiveConfig.SERVER.stabilizationTorqueConstant)
    forces.applyInvariantTorque(stabilizationTorque)

    if (linear) {
        val idealVelocity = Vector3d(vel).negate()
        idealVelocity.y = 0.0

        if (idealVelocity.lengthSquared() > (CreateInteractiveConfig.SERVER.linearStabilizeMaxAntiVelocity * CreateInteractiveConfig.SERVER.linearStabilizeMaxAntiVelocity))
            idealVelocity.normalize(CreateInteractiveConfig.SERVER.linearStabilizeMaxAntiVelocity)

        idealVelocity.mul(ship.inertia.shipMass * (10 - CreateInteractiveConfig.SERVER.antiVelocityMassRelevance))
        forces.applyInvariantForce(idealVelocity)
    }
}
