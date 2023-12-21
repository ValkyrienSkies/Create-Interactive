package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld

internal object MixinEntityLogic {
    internal fun postDiscard(entity: Entity) {
        if (entity !is AbstractContraptionEntity || entity is CarriageContraptionEntity) return
        val duck = entity as AbstractContraptionEntityDuck
        val shipId = duck.`ci$getShadowShipId`() ?: return
        val level = entity.level as? ServerLevel ?: return
        val serverShipWorldCore = level.shipObjectWorld
        val serverShip = serverShipWorldCore.allShips.getById(shipId) ?: return
        serverShipWorldCore.deleteShip(serverShip)
    }

    internal fun preOutOfWorld(entity: Entity, ci: CallbackInfo) {
        if (entity !is AbstractContraptionEntity) return
        val shipId = (entity as AbstractContraptionEntityDuck).`ci$getShadowShipId`()
        if (shipId != null) {
            // Don't kill contraption entities that are out of the world if they have a ship shadow
            ci.cancel()
        }
    }
}
