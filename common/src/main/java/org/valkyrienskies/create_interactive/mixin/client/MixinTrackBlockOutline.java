package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.trains.track.TrackBlockOutline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinTrackBlockOutlineLogic;

@Mixin(TrackBlockOutline.class)
public class MixinTrackBlockOutline {
    /**
     * Fixed train station direction wrong on ships
     */
    @Inject(method = "pickCurves", at = @At("RETURN"), remap = false)
    private static void postClientTick(final CallbackInfo ci) {
        MixinTrackBlockOutlineLogic.INSTANCE.postClientTick$create_interactive();
    }
}
