package org.valkyrienskies.create_interactive

import org.joml.Matrix4d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI

class ContraptionTransformsTest {
    private val claimCenter = Vector3d(1000000.0, 64.0, 2000000.0)
    private val half = Vector3d(0.5)

    private fun assertVector(expected: Vector3dc, actual: Vector3dc) {
        assertEquals(expected.x(), actual.x(), 1e-8)
        assertEquals(expected.y(), actual.y(), 1e-8)
        assertEquals(expected.z(), actual.z(), 1e-8)
    }

    @Test
    fun `shadow blocks follow Create block center rotation at all orientations and scales`() {
        val anchor = Vector3d(12.0, -4.0, 7.0)
        val pivot = claimCenter.add(half, Vector3d())
        val centerOfMass = claimCenter.add(3.25, -1.75, 0.125, Vector3d())
        val rotations = listOf(
            Quaterniond(), Quaterniond().rotateY(PI / 2), Quaterniond().rotateY(PI),
            Quaterniond().rotateXYZ(0.7, -1.2, 2.1)
        )
        for (rotation in rotations) {
            for (scale in listOf(0.5, 1.0, 2.0)) {
                val transform = ContraptionTransforms.create(anchor.add(half, Vector3d()), rotation, scale, pivot, centerOfMass)
                for (point in listOf(Vector3d(), Vector3d(0.5), Vector3d(2.5, -1.0, 7.25))) {
                    // Create.toGlobalVector: anchor + half + rotation * (localPoint - half).
                    val expected = Matrix4d().translate(anchor).translate(half).rotate(rotation).scale(scale)
                        .translate(-0.5, -0.5, -0.5).transformPosition(point, Vector3d())
                    val actual = transform.shipToWorld.transformPosition(claimCenter.add(point, Vector3d()), Vector3d())
                    assertVector(expected, actual)
                }
                assertVector(centerOfMass, transform.positionInShip)
            }
        }
    }

    @Test
    fun `client and server transforms agree even with different body origins`() {
        val pivot = claimCenter.add(half, Vector3d())
        val rotation = Quaterniond().rotateXYZ(0.3, 1.1, -0.8)
        val worldPivot = Vector3d(9.0, 12.0, -3.0)
        val server = ContraptionTransforms.create(worldPivot, rotation, 2.0, pivot, claimCenter.add(3.0, 2.0, 4.0, Vector3d()))
        val client = ContraptionTransforms.create(worldPivot, rotation, 2.0, pivot, pivot)
        val point = claimCenter.add(2.5, 0.5, -3.0, Vector3d())
        assertVector(server.shipToWorld.transformPosition(point, Vector3d()), client.shipToWorld.transformPosition(point, Vector3d()))
    }

    @Test
    fun `nested contraption matches composition with a rotated scaled parent`() {
        val parentRotation = Quaterniond().rotateXYZ(0.5, -0.3, 1.1)
        val parent = Matrix4d().translate(20.0, 3.0, -8.0).rotate(parentRotation).scale(2.0)
        val anchor = Vector3d(4.0, 6.0, -3.0)
        val rotation = Quaterniond().rotateX(1.2)
        val worldPivot = parent.transformPosition(anchor.add(half, Vector3d()), Vector3d())
        val transform = ContraptionTransforms.create(
            worldPivot, parentRotation.mul(rotation, Quaterniond()), 2.0,
            claimCenter.add(half, Vector3d()), claimCenter.add(1.25, 3.0, -2.0, Vector3d())
        )
        val localPoint = Vector3d(2.0, -1.0, 3.5)
        val expected = Matrix4d(parent).translate(anchor).translate(half).rotate(rotation).translate(-0.5, -0.5, -0.5)
            .transformPosition(localPoint, Vector3d())
        assertVector(expected, transform.shipToWorld.transformPosition(claimCenter.add(localPoint, Vector3d()), Vector3d()))
    }

    @Test
    fun `angular velocity uses world axes for a tilted ship`() {
        val previous = Quaterniond().rotateXYZ(0.7, 0.3, 1.2)
        val current = Quaterniond().rotateY(0.1).mul(previous)
        assertVector(Vector3d(0.0, 2.0, 0.0), ContraptionTransforms.angularVelocity(previous, current))
    }

    @Test
    fun `angular velocity uses the exact angle at high speeds`() {
        assertVector(
            Vector3d(PI * 10.0, 0.0, 0.0),
            ContraptionTransforms.angularVelocity(Quaterniond(), Quaterniond().rotateX(PI / 2))
        )
    }

    @Test
    fun `angular velocity crosses the quaternion sign boundary without a spike`() {
        val previous = Quaterniond().rotateY(179.0 * PI / 180.0)
        val current = Quaterniond().rotateY(-179.0 * PI / 180.0)
        assertVector(Vector3d(0.0, 2.0 * PI / 180.0 * 20.0, 0.0), ContraptionTransforms.angularVelocity(previous, current))
        assertVector(
            ContraptionTransforms.angularVelocity(previous, current),
            ContraptionTransforms.angularVelocity(previous, Quaterniond(-current.x(), -current.y(), -current.z(), -current.w()))
        )
    }

    @Test
    fun `unchanged rotation has zero angular velocity`() {
        val rotation = Quaterniond().rotateXYZ(0.7, -1.2, 0.4)
        assertVector(Vector3d(), ContraptionTransforms.angularVelocity(rotation, rotation))
    }
}
