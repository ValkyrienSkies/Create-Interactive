package org.valkyrienskies.create_interactive.forge.mixin;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.valkyrienskies.create_interactive.forge.mixinducks.CombinedInvWrapperDuck;

import java.util.Collection;

@Mixin(CombinedInvWrapper.class)
public class MixinCombinedInvWrapper implements CombinedInvWrapperDuck {
    @Final
    @Shadow
    @Mutable
    protected IItemHandlerModifiable[] itemHandler;
    @Final
    @Shadow
    @Mutable
    protected int[] baseIndex;
    @Final
    @Shadow
    @Mutable
    protected int slotCount;
    @Override
    public void ci$setInventories(final Collection<IItemHandlerModifiable> itemHandlerCollection) {
        this.itemHandler = itemHandlerCollection.toArray(new IItemHandlerModifiable[0]);
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++) {
            index += itemHandler[i].getSlots();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }
}
