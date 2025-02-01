package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import io.netty.util.collection.LongObjectHashMap
import io.netty.util.collection.LongObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import it.unimi.dsi.fastutil.longs.LongSet
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
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
import org.valkyrienskies.create_interactive.config.CreateInteractiveConfigs
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.create_interactive.mixinducks.OrientedContraptionEntityDuck
import org.valkyrienskies.create_interactive.services.NoOptimize
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.isBlockInShipyard
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

            val duck = contraptionEntityCopy as AbstractContraptionEntityDuck
            if (!duck.`ci$hasTickedThisTick`()) {
                // This can happen when trains get relocated by turn-tables, tick the entity to move it back to the correct location
                contraptionEntityCopy.tick()
            }
            duck.`ci$resetHasTickedThisTick`()

            // Skip the ship if its null, but don't delete the map entry in case the ship packet was delayed
            val clientShip = shipObjectWorld.allShips.getById(shipId) ?: continue

            val shipCenter: Vector3ic = clientShip.getChunkClaimCenterPos(mc.level!!)

            if (contraptionEntityCopy is CarriageContraptionEntity && isTrainDerailed(contraptionEntityCopy)) {
                CreateInteractiveUtil.moveContraptionToTransform(contraptionEntityCopy, clientShip)
            } else if (contraptionEntityCopy is OrientedContraptionEntityDuck) {
                contraptionEntityCopy.`ci$setForcedRotation`(null)
            }

            clientShip.transformProvider = object : ClientShipTransformProvider {
                @NoOptimize
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

                        val (first, second, scale) = getContraptionPosRot(contraptionEntity)

                        if (contraptionEntityCopy.level().isBlockInShipyard(first.x(), first.y(), first.z())) {
                            if (contraptionEntityCopy.level().getShipManagingPos(first) == null) {
                                // Ignore it to fix train turntables being strange when going to be world
                                return null
                            }
                        }

                        // The contraption center block is at the same position as the ship center, so create the
                        // transform to apply that
                        // TODO: Use CreateInteractiveUtil.posRotToShipTransform()
                        return ShipTransformImpl.create(
                            first, Vector3d(shipCenter).add(0.5, 0.5, 0.5), second, Vector3d(scale)
                        )
                    }
                    return null
                }

                @NoOptimize
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

                        val position = contraptionEntity.position()
                        val parentShip = contraptionEntity.level().getShipManagingPos(position) as ShipObjectClient?
                        if (contraptionEntityCopy.level().isBlockInShipyard(position) && parentShip == null) {
                            // Ignore it to fix train turntables being strange when going to be world
                            return null
                        }
                        if (parentShip == clientShip) {
                            // Happens when you place a train on itself
                            return null
                        }
                        if (parentShip != null && !updatedShips.contains(parentShip.id)) {
                            parentShip.updateRenderShipTransform(partialTick)
                            updatedShips.add(parentShip.id)
                        }
                        val (first, second, scale) = getContraptionPosRotForRender(contraptionEntity, partialTick)
                        // TODO: Use CreateInteractiveUtil.posRotToShipTransform()
                        val renderTransform = ShipTransformImpl.create(
                            first, Vector3d(shipCenter).add(0.5, 0.5, 0.5), second, Vector3d(scale)
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

    /**
     * Registered as an event seperately on forge and fabric
     */
    fun onPlayerJoin(player: ServerPlayer) {
        if (!CreateInteractiveConfigs.common().disableChatWarning.get()) {
            player.sendSystemMessage(
                // Should we make this a translatable?
                Component.literal(
                    "Create: Interactive is installed. Please report bugs to Interactive BEFORE reporting them to Create. You can disable this message in the Interactive common config. (Servers need to set this separately)"
                ).withStyle(
                    ChatFormatting.YELLOW
                )
            );
        }
    }
}
