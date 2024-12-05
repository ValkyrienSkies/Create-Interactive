package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DeployerBlockEntity.class)
public interface DeployerBlockEntityAccessor {
    @Accessor(value = "filtering",remap = false)
    FilteringBehaviour getFiltering();

    @Accessor(value = "reach",remap = false)
    float getReach();
}
