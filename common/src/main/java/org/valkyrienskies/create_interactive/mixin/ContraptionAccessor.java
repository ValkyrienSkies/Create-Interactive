package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * This accessor exists because proguard is having trouble accessing Contraption fields in kotlin
 */
@Mixin(Contraption.class)
public interface ContraptionAccessor {
    @Accessor("stalled")
    boolean getStalled();

    @Accessor("stalled")
    void setStalled(boolean stalled);
}
