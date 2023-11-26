package org.valkyrienskies.create_interactive.mixin_logic

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld

internal object MixinEntityLogic {
    internal fun postDiscard(thisAs: Entity) {
        val duck = thisAs as AbstractContraptionEntityDuck
        val shipId = duck.`ci$getShadowShipId`() ?: return
        val level = thisAs.level as? ServerLevel ?: return
        val serverShipWorldCore = level.shipObjectWorld
        val serverShip = serverShipWorldCore.allShips.getById(shipId) ?: return
        serverShipWorldCore.deleteShip(serverShip)
    }
}
