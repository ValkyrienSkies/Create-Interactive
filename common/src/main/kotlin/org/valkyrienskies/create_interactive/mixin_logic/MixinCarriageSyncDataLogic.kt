package org.valkyrienskies.create_interactive.mixin_logic

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.CarriageSyncData
import com.simibubi.create.content.trains.graph.TrackEdge
import com.simibubi.create.content.trains.graph.TrackGraph
import com.simibubi.create.content.trains.graph.TrackNode
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.mixin.CarriageSyncDataAccessor

internal object MixinCarriageSyncDataLogic {
    internal fun preUpdateFallbackLocations(carriageSyncData: CarriageSyncData, dce: Carriage.DimensionalCarriageEntity, ci: CallbackInfo) {
        if (dce.rotationAnchors.any { it == null }) {
            (carriageSyncData as CarriageSyncDataAccessor).setFallbackLocations(null)
            carriageSyncData.isDirty = true
            ci.cancel()
        }
    }

    internal fun redirectGetConnectionsFromInGetDistanceTo(
        instance: TrackGraph,
        node: TrackNode,
        operation: Operation<Map<TrackNode?, TrackEdge?>>,
    ): Map<TrackNode?, TrackEdge?> {
        return operation.call(instance, node) ?: emptyMap()
    }
}
