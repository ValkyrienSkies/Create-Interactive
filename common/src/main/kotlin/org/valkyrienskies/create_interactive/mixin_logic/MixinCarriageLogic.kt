package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.ContraptionPosRot
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.teleportShipToPosRot
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML

internal object MixinCarriageLogic {
    internal fun doesCarriageEntityControlShip(
        entity: CarriageContraptionEntity, newShadowShipId: ShipId?, shipDimension: ResourceKey<Level>?,
        oldShadowShipId: ShipId?, setShipShadow: (ShipId?) -> Unit, setShipDimension: (ResourceKey<Level>?) -> Unit,
    ): Boolean {
        if (newShadowShipId == null) {
            return false
        }
        setShipShadow(newShadowShipId)
        if (shipDimension == null) {
            setShipDimension(entity.level.dimension())
            return true
        }
        return shipDimension == entity.level.dimension()
    }

    internal fun postManageEntities(
        level: Level, shadowShipId: ShipId?, shipDimension: ResourceKey<Level>?, entities: Map<ResourceKey<Level>, Carriage.DimensionalCarriageEntity>, setShipDimension: (ResourceKey<Level>?) -> Unit
    ) {
        if (level.isClientSide || shadowShipId == null) return
        if (!entities.containsKey(shipDimension)) {
            // Choose a new entity to give ownership of the ship
            for ((key, value) in entities.entries)  {
                val actualEntity: AbstractContraptionEntity? = value.entity.get()
                if (actualEntity != null) {
                    // Set this entity to be the shipowner
                    setShipDimension(key)
                    (actualEntity as AbstractContraptionEntityDuck).`ci$setShadowShipId`(shadowShipId)
                    return
                }
            }
            // We can't find an entity to attach this to? Just move the ship anyway.
            val (key, value) = entities.entries.iterator().next()
            setShipDimension(key)

            // This is fine because there is only 1 ship object world per server, so any level is valid for this
            val serverShip: ServerShip =
                (level as ServerLevel).shipObjectWorld.allShips.getById(shadowShipId) ?: return
            val pos: Vector3dc = value.positionAnchor.toJOML()
            // Not sure what to do for rot tbh, but this will work for now
            val rot: Quaterniondc = Quaterniond()
            val posRot = ContraptionPosRot(pos, rot, 1.0)
            val destLevel = level.server.getLevel(key)
            teleportShipToPosRot(posRot, serverShip, destLevel!!)
        }
    }
}
