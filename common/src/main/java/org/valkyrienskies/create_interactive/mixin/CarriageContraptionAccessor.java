package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.CarriageContraption;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CarriageContraption.class)
public interface CarriageContraptionAccessor {
    @Accessor(value = "assembledBlockConductors", remap = false)
    List<BlockPos> getAssembledBlazeBurners();

    @Accessor(value = "assembledBlockConductors", remap = false)
    void setAssembledBlazeBurners(List<BlockPos> assembledBlazeBurners);
}
