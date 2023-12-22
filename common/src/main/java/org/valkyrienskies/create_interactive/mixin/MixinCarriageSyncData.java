package org.valkyrienskies.create_interactive.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageSyncData;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinCarriageSyncDataLogic;

import java.util.Map;

@Mixin(CarriageSyncData.class)
public class MixinCarriageSyncData {
    /**
     * @author Tri0de
     * @reason Fix train cars crashing when partially through portals
     */
    @Inject(method = "updateFallbackLocations", at = @At("HEAD"), cancellable = true, remap = false)
    private void preUpdateFallbackLocations(final Carriage.DimensionalCarriageEntity dce, final CallbackInfo ci) {
        MixinCarriageSyncDataLogic.INSTANCE.preUpdateFallbackLocations$create_interactive(CarriageSyncData.class.cast(this), dce, ci);
    }

    /**
     * Fix create bug that causes re-railing initially derailed trains to fail on client-side
     */
    @WrapOperation(
        method = "getDistanceTo",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/trains/graph/TrackGraph;getConnectionsFrom(Lcom/simibubi/create/content/trains/graph/TrackNode;)Ljava/util/Map;"
        ),
        remap = false
    )
    private Map<TrackNode, TrackEdge> redirectGetConnectionsFromInGetDistanceTo(
        final TrackGraph instance,
        final TrackNode node,
        final Operation<Map<TrackNode, TrackEdge>> operation
    ) {
        return MixinCarriageSyncDataLogic.INSTANCE.redirectGetConnectionsFromInGetDistanceTo$create_interactive(instance, node, operation);
    }
}
