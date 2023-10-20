package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(ContraptionCollider.class)
public class MixinContraptionCollider {
    /**
     * Disable contraption collision entirely! We will get it from VS2 instead!
     */
    @Inject(method = "collideEntities", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preCollideEntities(final AbstractContraptionEntity contraptionEntity, final CallbackInfo ci) {
        // Only disable collision if the ship isn't here yet
        final Long shipId = ((AbstractContraptionEntityDuck) contraptionEntity).getShadowShipId();
        if (shipId == null) return;
        final Ship ship = VSGameUtilsKt.getShipObjectWorld(contraptionEntity.level).getLoadedShips().getById(shipId);
        if (ship == null) return;
        ci.cancel();
    }
}
