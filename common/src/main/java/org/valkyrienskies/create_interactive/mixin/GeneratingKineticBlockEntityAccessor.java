package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GeneratingKineticBlockEntity.class)
public interface GeneratingKineticBlockEntityAccessor {
    @Accessor(value = "reActivateSource",remap = false)
    boolean getReActivateSource();

    @Accessor(value = "reActivateSource",remap = false)
    void setReActivateSource(boolean reActivateSource);
}
