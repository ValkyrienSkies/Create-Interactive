package org.valkyrienskies.create_interactive

import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.bodies.properties.BodyTransformImpl
import kotlin.math.atan2
import kotlin.math.sqrt

/** Coordinate math shared by server physics, teleports, and client rendering. */
internal object ContraptionTransforms {
    fun create(
        pivotInWorld: Vector3dc,
        rotation: Quaterniondc,
        scale: Double,
        pivotInShip: Vector3dc,
        bodyOriginInShip: Vector3dc,
    ): ShipTransform {
        // Create rotates around the center of the anchor block, whereas VS rotates around the body's origin.
        val offset = bodyOriginInShip.sub(pivotInShip, Vector3d()).mul(scale)
        rotation.transform(offset)
        return BodyTransformImpl(
            pivotInWorld.add(offset, Vector3d()),
            Quaterniond(rotation),
            Vector3d(scale),
            Vector3d(bodyOriginInShip),
        )
    }

    fun angularVelocity(previous: Quaterniondc, current: Quaterniondc): Vector3dc {
        // VS uses world-space radians/second: current = deltaWorld * previous.
        val delta = current.mul(previous.conjugate(Quaterniond()), Quaterniond()).normalize()
        // q and -q describe the same orientation. Choose the shortest rotation across the tick.
        val sign = if (delta.w() < 0.0) -1.0 else 1.0
        val sinHalfAngle = sqrt(delta.x() * delta.x() + delta.y() * delta.y() + delta.z() * delta.z())
        val factor = if (sinHalfAngle < 1e-12) 40.0 else
            40.0 * atan2(sinHalfAngle, delta.w() * sign) / sinHalfAngle
        return Vector3d(delta.x(), delta.y(), delta.z()).mul(sign * factor)
    }
}
