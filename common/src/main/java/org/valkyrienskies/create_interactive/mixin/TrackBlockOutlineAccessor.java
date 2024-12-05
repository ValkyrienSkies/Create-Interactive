package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.track.TrackBlockOutline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TrackBlockOutline.class)
public interface TrackBlockOutlineAccessor {
    @Accessor(value = "result", remap = false)
    static TrackBlockOutline.BezierPointSelection getResult() {
        throw new IllegalStateException();
    }

    @Accessor(value = "result",remap = false)
    static void setResult(TrackBlockOutline.BezierPointSelection result) {
        throw new IllegalStateException();
    }
}
