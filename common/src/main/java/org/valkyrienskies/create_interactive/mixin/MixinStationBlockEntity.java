package org.valkyrienskies.create_interactive.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.entity.CarriageContraption;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StationBlockEntity.class)
public class MixinStationBlockEntity {

    // You aren't required to add controls to the trains since you can just add them afterwards
    @WrapOperation(
        method = "assemble",
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/CarriageContraption;hasForwardControls()Z"),
        remap = false
    )
    private boolean skipControls(CarriageContraption instance, Operation<Boolean> operation) {
        return true;
    }

    @WrapOperation(
        method = "assemble",
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/CarriageContraption;assemble(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z"),
        remap = false
    )
    private boolean emptyCarriage(CarriageContraption instance, Level level, BlockPos pos, Operation<Boolean> operation) {
        operation.call(level, pos);
        return true;
    }
}
