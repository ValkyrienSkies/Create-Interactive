package org.valkyrienskies.create_interactive.mixin;

import net.minecraft.core.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * This mixin allows us to fix TrackNodeLocation that are created incorrectly
 */
@Mixin(Vec3i.class)
public interface Vec3iAccessor {
    @Accessor("x")
    @Mutable
    void setX(int x);

    @Accessor("y")
    @Mutable
    void setY(int y);

    @Accessor("z")
    @Mutable
    void setZ(int z);
}
