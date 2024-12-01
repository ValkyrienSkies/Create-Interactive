package org.valkyrienskies.create_interactive.mixin.contraptins;

import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ControlledContraptionEntity.class)
public interface ControlledContraptionEntityAccessor {
    @Invoker(value = "getController", remap = false)
    IControlContraption invokeGetController();
}
