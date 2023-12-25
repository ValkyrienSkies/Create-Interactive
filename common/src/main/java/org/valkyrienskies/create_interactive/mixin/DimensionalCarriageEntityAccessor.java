package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.Carriage;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Carriage.DimensionalCarriageEntity.class)
public interface DimensionalCarriageEntityAccessor {
    @Accessor("positionAnchor")
    void setPositionAnchor(final Vec3 positionAnchor);
}
