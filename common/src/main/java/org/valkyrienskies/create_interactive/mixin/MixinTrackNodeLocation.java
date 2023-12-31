package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.MixinTrackNodeLocationLogic;

@Mixin(TrackNodeLocation.class)
public class MixinTrackNodeLocation {
    /**
     * This mixin fixes TrackNodeLocation not working in the shipyard
     */
    @Inject(method = "<init>(DDD)V", at = @At("RETURN"))
    private void postInit(final double x, final double y, final double z, final CallbackInfo ci) {
        MixinTrackNodeLocationLogic.INSTANCE.postInit$create_interactive(TrackNodeLocation.class.cast(this), x, y, z);
    }
}
