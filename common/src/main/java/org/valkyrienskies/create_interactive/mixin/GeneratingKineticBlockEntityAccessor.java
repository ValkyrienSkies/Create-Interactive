package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GeneratingKineticBlockEntity.class)
public interface GeneratingKineticBlockEntityAccessor {
    @Accessor("reActivateSource")
    boolean getReActivateSource();

    @Accessor("reActivateSource")
    void setReActivateSource(boolean reActivateSource);
}
