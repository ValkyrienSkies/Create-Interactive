package org.valkyrienskies.create_interactive.mixin_logic.client

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.simibubi.create.AllItems
import com.simibubi.create.content.contraptions.ContraptionHandler
import com.simibubi.create.content.contraptions.ContraptionHandlerClient
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import com.simibubi.create.content.trains.entity.TrainRelocator
import com.simibubi.create.foundation.utility.VecHelper
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.multiplayer.MultiPlayerGameMode
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient.postTickClient
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinMinecraftLogic {
    internal fun postTick(pause: Boolean, level: ClientLevel?, connection: ClientPacketListener?) {
        if (!pause && level != null && connection != null) {
            postTickClient()
        }
    }

    /**
     * Fix train wrenching by doing train wrenching after block interactions
     */
    internal fun wrapUseItemOn(
        gameMode: MultiPlayerGameMode,
        player: LocalPlayer,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult,
        operation: Operation<InteractionResult>,
    ): InteractionResult {
        val result = operation.call(gameMode, player, interactionHand, blockHitResult)
        if (!result.consumesAction() && AllItems.WRENCH.isIn(player.getItemInHand(interactionHand))) {
            // Consider a train relocation
            val rayInputs = ContraptionHandlerClient.getRayInputs(player)
            val origin = rayInputs.first
            val target = rayInputs.second

            val aabb = AABB(origin, target).inflate(16.0)

            val contraptionsInLevel = ContraptionHandler.loadedContraptions.get(player.level).values

            contraptionsInLevel.mapNotNull { it.get() as? CarriageContraptionEntity? }.forEach { contraptionEntity ->
                val ship = player.level.getShipManagingPos(contraptionEntity.anchorVec)
                if (ship != null) {
                    val transform = if (ship is ClientShip) ship.renderTransform else ship.transform
                    val newAABB = aabb.toJOML().transform(transform.worldToShip).toMinecraft()
                    if (!contraptionEntity.boundingBox.intersects(newAABB)) {
                        return@forEach
                    }
                } else {
                    if (!contraptionEntity.boundingBox.intersects(aabb)) {
                        return@forEach
                    }
                }

                // Effectively do ContraptionHandlerClient.handleSpecialInteractions()
                val rayTraceResult =
                    ContraptionHandlerClient.rayTraceContraption(origin, target, contraptionEntity) ?: return@forEach

                TrainRelocator.carriageWrenched(
                    contraptionEntity.toGlobalVector(VecHelper.getCenterOf(rayTraceResult.blockPos), 1.0f),
                    contraptionEntity
                )
            }
        }
        return result
    }
}
