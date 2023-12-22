package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmartBlockEntity.class)
public interface SmartBlockEntityAccessor {

    @Accessor("initialized")
    boolean getInitialized();

    @Accessor("initialized")
    void setInitialized(boolean initialized);
}
