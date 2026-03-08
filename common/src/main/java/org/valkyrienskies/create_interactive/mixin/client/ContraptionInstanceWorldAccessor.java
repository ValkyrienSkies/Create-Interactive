package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionWorld;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Contraption.class)
public interface ContraptionInstanceWorldAccessor {
    @Accessor(value = "collisionLevel",remap = false)
    ContraptionWorld getBlockEntityInstanceManager();
}
