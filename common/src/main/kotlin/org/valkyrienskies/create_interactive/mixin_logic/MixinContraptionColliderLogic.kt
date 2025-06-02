package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.doesContraptionHaveShipLoaded

internal object MixinContraptionColliderLogic {
    internal fun preCollideEntities(contraptionEntity: AbstractContraptionEntity?, ci: CallbackInfo) {
        // Only disable collision if the contraption is loaded (isn't null)
        if (contraptionEntity == null) return;

        // Only disable collision if the contraption has a ship
        if (doesContraptionHaveShipLoaded(contraptionEntity.contraption)) {
            ci.cancel()
        }
    }
}
