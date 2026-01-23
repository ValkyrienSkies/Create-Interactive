package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.CarriageSyncData;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CarriageSyncData.class)
public interface CarriageSyncDataAccessor {
    @Accessor(value = "fallbackLocations",remap = false)
    void setFallbackLocations(Pair<Vec3, Couple<Vec3>> fallbackLocations);
}
