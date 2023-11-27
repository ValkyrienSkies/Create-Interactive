package org.valkyrienskies.create_interactive.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructureTemplate.StructureBlockInfo.class)
public interface StructureBlockInfoAccessor {
    @Accessor(value = "nbt", remap = false)
    @Mutable
    void setNbt(CompoundTag nbt);
}
