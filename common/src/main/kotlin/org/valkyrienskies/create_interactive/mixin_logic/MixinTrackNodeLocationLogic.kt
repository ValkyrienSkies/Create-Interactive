package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.graph.TrackNodeLocation
import org.valkyrienskies.create_interactive.mixin.Vec3iAccessor
import kotlin.math.roundToInt

internal object MixinTrackNodeLocationLogic {
    /**
     * This mixin fixes TrackNodeLocation not working in the shipyard
     */
    internal fun postInit(trackLocation: TrackNodeLocation, x: Double, y: Double, z: Double) {
        val newX = (x * 2.0).roundToInt()
        val newY = (y * 2.0).roundToInt()
        val newZ = (z * 2.0).roundToInt()

        trackLocation as Vec3iAccessor
        trackLocation.setX(newX)
        trackLocation.setY(newY)
        trackLocation.setZ(newZ)
    }
}
