package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmartBlockEntity.class)
public interface SmartBlockEntityAccessor {

    @Accessor(value = "initialized",remap = false)
    boolean getInitialized();

    @Accessor(value = "initialized",remap = false)
    void setInitialized(boolean initialized);

    @Accessor(value = "lazyTickCounter",remap = false)
    int getLazyTickCounter();

    @Accessor(value = "lazyTickCounter",remap = false)
    void setLazyTickCounter(int lazyTickCounter);

    @Accessor(value = "lazyTickRate",remap = false)
    int getLazyTickRate();
}
