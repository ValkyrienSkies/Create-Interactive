package org.valkyrienskies.create_interactive.forge.mixin;

import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.forge.mixinducks.CombinedTankWrapperDuck;

import java.util.Collection;

@Mixin(CombinedTankWrapper.class)
public class MixinCombinedTankWrapper implements CombinedTankWrapperDuck {
    @Final
    @Shadow(remap = false)
    @Mutable
    protected IFluidHandler[] itemHandler;
    @Final
    @Shadow(remap = false)
    @Mutable
    protected int[] baseIndex;
    @Final
    @Shadow(remap = false)
    @Mutable
    protected int tankCount;

    @Override
    public void ci$setInventories(final Collection<IFluidHandler> fluidHandlerCollection) {
        this.itemHandler = fluidHandlerCollection.toArray(new IFluidHandler[0]);
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++) {
            index += itemHandler[i].getTanks();
            baseIndex[i] = index;
        }
        this.tankCount = index;
    }
}
