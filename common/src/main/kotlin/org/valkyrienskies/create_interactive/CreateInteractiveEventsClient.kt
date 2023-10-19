package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import io.netty.util.collection.LongObjectHashMap
import io.netty.util.collection.LongObjectMap
import net.minecraft.client.Minecraft
import org.valkyrienskies.core.api.ships.ClientShipTransformProvider
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.world.ClientShipWorldCore
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionPosRot
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider
import java.lang.ref.WeakReference

object CreateInteractiveEventsClient {
    private val shipToContraptions: LongObjectMap<WeakReference<AbstractContraptionEntity>> = LongObjectHashMap()

    fun postTickClient() {
        val mc = Minecraft.getInstance()
        // Tick the ship world and then drag entities
        val shipObjectWorld: ClientShipWorldCore = (mc as IShipObjectWorldClientProvider).shipObjectWorld!!

        val it: MutableIterator<LongObjectMap.PrimitiveEntry<WeakReference<AbstractContraptionEntity>>> = shipToContraptions.entries().iterator()
        while (it.hasNext()) {
            val next = it.next()
            val shipId = next.key()
            val contraption = next.value()
            // Remove stale references
            if (contraption.get() == null) {
                it.remove()
                continue
            }
            // Skip the ship if its null, but don't delete the map entry in case the ship packet was delayed
            val clientShip = shipObjectWorld.allShips.getById(shipId) ?: continue
            clientShip.transformProvider = object : ClientShipTransformProvider {
                override fun provideNextTransform(
                    prevShipTransform: ShipTransform,
                    shipTransform: ShipTransform,
                    latestNetworkTransform: ShipTransform
                ): ShipTransform? {
                    val contraptionEntity = contraption.get()
                    if (contraptionEntity != null) {
                        val (first, second) = getContraptionPosRot(contraptionEntity)
                        return ShipTransformImpl.create(
                            first, shipTransform.positionInShip, second
                        )
                    }
                    return null
                }

                override fun provideNextRenderTransform(
                    prevShipTransform: ShipTransform,
                    shipTransform: ShipTransform,
                    partialTick: Double
                ): ShipTransform? {
                    // Don't override default behavior
                    return null
                }
            }
        }
    }

    fun addShipToContraptionRef(shipId: ShipId, contraptionEntity: AbstractContraptionEntity) {
        shipToContraptions[shipId] = WeakReference(contraptionEntity)
    }
}
