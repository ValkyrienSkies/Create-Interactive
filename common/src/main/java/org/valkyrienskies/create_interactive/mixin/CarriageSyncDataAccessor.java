package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.CarriageSyncData;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CarriageSyncData.class)
public interface CarriageSyncDataAccessor {
    @Accessor("fallbackLocations")
    void setFallbackLocations(Pair<Vec3, Couple<Vec3>> fallbackLocations);
}
