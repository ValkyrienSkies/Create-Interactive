package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import io.netty.util.collection.LongObjectHashMap
import io.netty.util.collection.LongObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.AABB
import org.joml.Vector3d
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.ClientShipTransformProvider
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.apigame.world.ClientShipWorldCore
import org.valkyrienskies.core.impl.game.ships.ShipObjectClient
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionPosRot
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getContraptionPosRotForRender
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.isTrainDerailed
import org.valkyrienskies.create_interactive.mixinducks.OrientedContraptionEntityDuck
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider
import org.valkyrienskies.mod.common.getShipManagingPos
import java.lang.ref.WeakReference

object CreateInteractiveEventsClient {
    private val shipToContraptions: LongObjectMap<WeakReference<AbstractContraptionEntity>> = LongObjectHashMap()
    private val updatedShips: LongSet = LongOpenHashSet()
    private val cachedRenderTransforms: Long2ObjectMap<ShipTransform> = Long2ObjectOpenHashMap()

    internal fun postTickClient() {
        val mc = Minecraft.getInstance()
        // Tick the ship world and then drag entities
        val shipObjectWorld: ClientShipWorldCore = (mc as IShipObjectWorldClientProvider).shipObjectWorld!!

        val it: MutableIterator<LongObjectMap.PrimitiveEntry<WeakReference<AbstractContraptionEntity>>> =
            shipToContraptions.entries().iterator()

        while (it.hasNext()) {
            val next = it.next()
            val shipId = next.key()
            val contraption = next.value()

            // Remove stale references
            val contraptionEntityCopy = contraption.get()
            if (contraptionEntityCopy == null) {
                it.remove()
                continue
            }

            // Skip the ship if its null, but don't delete the map entry in case the ship packet was delayed
            val clientShip = shipObjectWorld.allShips.getById(shipId)

            if (clientShip == null) {
                // Only apply this logic to carriages that have been derailed
                if (contraptionEntityCopy is CarriageContraptionEntity && contraptionEntityCopy.carriage.train.derailed) {
                    // If the client ship isn't loaded then send this contraption to Brazil.
                    // This fixes the bug of trains popping in and out of existence when they deviate too far from their
                    // original tracks.
                    val brazil = 1e6
                    contraptionEntityCopy.setPos(contraptionEntityCopy.x, brazil, contraptionEntityCopy.z)
                    contraptionEntityCopy.boundingBox = AABB(
                        contraptionEntityCopy.x, brazil, contraptionEntityCopy.z,
                        contraptionEntityCopy.x, brazil, contraptionEntityCopy.z
                    )
                }
                continue
            }

            val shipCenter: Vector3ic = clientShip.getChunkClaimCenterPos(mc.level!!)

            if (contraptionEntityCopy is CarriageContraptionEntity && isTrainDerailed(contraptionEntityCopy)) {
                CreateInteractiveUtil.moveContraptionToTransform(contraptionEntityCopy, clientShip)
            } else if (contraptionEntityCopy is OrientedContraptionEntityDuck) {
                contraptionEntityCopy.`ci$setForcedRotation`(null)
            }

            clientShip.transformProvider = object : ClientShipTransformProvider {
                override fun provideNextTransform(
                    prevShipTransform: ShipTransform,
                    shipTransform: ShipTransform,
                    latestNetworkTransform: ShipTransform
                ): ShipTransform? {
                    val contraptionEntity = contraption.get()
                    if (contraptionEntity != null) {
                        // Derailed trains can move freely
                        if (contraptionEntity is CarriageContraptionEntity && isTrainDerailed(contraptionEntity)) {
                            return null
                        }

                        val (first, second) = getContraptionPosRot(contraptionEntity)

                        // The contraption center block is at the same position as the ship center, so create the
                        // transform to apply that
                        // TODO: Use CreateInteractiveUtil.posRotToShipTransform()
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
                    if (cachedRenderTransforms.contains(shipId)) {
                        return cachedRenderTransforms[shipId]
                    }
                    val contraptionEntity = contraption.get()
                    if (contraptionEntity != null) {
                        // Derailed trains can move freely
                        if (contraptionEntity is CarriageContraptionEntity && isTrainDerailed(contraptionEntity)) {
                            return null
                        }

                        val parentShip = contraptionEntity.level.getShipManagingPos(contraptionEntity.position()) as ShipObjectClient?
                        if (parentShip == clientShip) {
                            // Happens when you place a train on itself
                            return null
                        }
                        if (parentShip != null && !updatedShips.contains(parentShip.id)) {
                            parentShip.updateRenderShipTransform(partialTick)
                            updatedShips.add(parentShip.id)
                        }
                        val (first, second) = getContraptionPosRotForRender(contraptionEntity, partialTick)
                        // TODO: Use CreateInteractiveUtil.posRotToShipTransform()
                        val renderTransform = ShipTransformImpl.create(
                            first, Vector3d(shipCenter).add(0.5, 0.5, 0.5), second
                        )
                        cachedRenderTransforms[shipId] = renderTransform
                        return renderTransform
                    }
                    return null
                }
            }
        }
    }

    internal fun onStartUpdateRenderTransforms() {
        updatedShips.clear()
        cachedRenderTransforms.clear()
    }

    internal fun addShipToContraptionRef(shipId: ShipId, contraptionEntity: AbstractContraptionEntity) {
        shipToContraptions[shipId] = WeakReference(contraptionEntity)
    }
}
