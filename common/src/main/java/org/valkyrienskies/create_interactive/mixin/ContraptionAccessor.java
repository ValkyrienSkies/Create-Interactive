package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * This accessor exists because proguard is having trouble accessing Contraption fields in kotlin
 */
@Mixin(Contraption.class)
public interface ContraptionAccessor {
    @Accessor("stalled")
    boolean getStalled();

    @Accessor("stalled")
    void setStalled(boolean stalled);

    @Invoker("getBlockEntityNBT")
    CompoundTag invokeGetBlockEntityNBT(Level world, BlockPos pos);
}
