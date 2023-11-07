package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractContraptionEntity.class)
public interface AbstractContraptionEntityAccessor {
    @Invoker("onContraptionStalled")
    void invokeOnContraptionStalled();

    @Accessor("STALLED")
    EntityDataAccessor<Boolean> getStalled();

    @Accessor("contraption")
    Contraption getContraption();
}
