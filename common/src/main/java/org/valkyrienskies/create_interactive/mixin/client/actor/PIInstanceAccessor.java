package org.valkyrienskies.create_interactive.mixin.client.actor;

import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.simibubi.create.content.contraptions.actors.psi.PIInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PIInstance.class)
public interface PIInstanceAccessor {
    @Accessor("middle")
    ModelData getMiddle();
    @Accessor("top")
    ModelData getTop();
}
