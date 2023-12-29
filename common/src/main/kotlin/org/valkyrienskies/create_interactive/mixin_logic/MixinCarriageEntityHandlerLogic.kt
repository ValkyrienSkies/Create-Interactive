package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.world.entity.Entity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object MixinCarriageEntityHandlerLogic {
    // TODO: Only skip these if the carriage entity has relocated recently
    fun preOnEntityEnterSection(entity: Entity, ci: CallbackInfo) {
        if (entity !is CarriageContraptionEntity) return
        ci.cancel()
    }

    // TODO: Only skip these if the carriage entity has relocated recently
    fun preValidateCarriageEntity(entity: CarriageContraptionEntity, ci: CallbackInfo) {
        if (!entity.isAlive) return
        ci.cancel()
    }
}
