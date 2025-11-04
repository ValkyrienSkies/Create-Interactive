package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.simibubi.create.content.contraptions.actors.psi.PIInstance;
import dev.engine_room.flywheel.lib.instance.TransformedInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PIInstance.class)
public interface PIInstanceAccessor {
    @Accessor(value = "middle",remap = false)
    TransformedInstance getMiddle();
    @Accessor(value = "top",remap = false)
    TransformedInstance getTop();
}
