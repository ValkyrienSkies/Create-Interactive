package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import io.netty.util.collection.LongObjectHashMap
import io.netty.util.collection.LongObjectMap
import net.minecraft.client.Minecraft
import org.joml.Vector3d
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.ClientShipTransformProvider
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.world.ClientShipWorldCore
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
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
            val shipCenter: Vector3ic = clientShip.getChunkClaimCenterPos(mc.level!!)
            clientShip.transformProvider = object : ClientShipTransformProvider {
                override fun provideNextTransform(
                    prevShipTransform: ShipTransform,
                    shipTransform: ShipTransform,
                    latestNetworkTransform: ShipTransform
                ): ShipTransform? {
                    val contraptionEntity = contraption.get()
                    if (contraptionEntity != null) {
                        val (first, second) = getContraptionPosRot(contraptionEntity)

                        // The contraption center block is at the same position as the ship center, so create the
                        // transform to apply that
                        return ShipTransformImpl.create(
                            first, Vector3d(shipCenter).add(0.5, 0.5, 0.5), second
                        )
                    }
                    return null
                }

                override fun provideNextRenderTransform(
                    prevShipTransform: ShipTransform,
                    shipTransform: ShipTransform,
                    partialTick: Double
                ): ShipTransform? {
                    // println("prevShipTransform is ${prevShipTransform.positionInShip.x()}, ${prevShipTransform.positionInShip.y()}, ${prevShipTransform.positionInShip.z()}")
                    // println("shipTransform is ${shipTransform.positionInShip.x()}, ${shipTransform.positionInShip.y()}, ${shipTransform.positionInShip.z()}")
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
