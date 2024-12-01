package org.valkyrienskies.create_interactive.mixin.client;

import com.simibubi.create.content.contraptions.render.ContraptionInstanceManager;
import com.simibubi.create.content.contraptions.render.FlwContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FlwContraption.ContraptionInstanceWorld.class)
public interface ContraptionInstanceWorldAccessor {
    @Accessor(value = "blockEntityInstanceManager",remap = false)
    ContraptionInstanceManager getBlockEntityInstanceManager();
}
