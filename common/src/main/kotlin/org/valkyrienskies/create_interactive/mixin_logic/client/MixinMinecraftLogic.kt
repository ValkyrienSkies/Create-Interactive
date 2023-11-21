package org.valkyrienskies.create_interactive.mixin_logic.client

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.simibubi.create.AllItems
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
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
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient.postTickClient
import java.lang.ref.WeakReference

internal object MixinMinecraftLogic {
    internal fun postTick(pause: Boolean, level: ClientLevel?, connection: ClientPacketListener?) {
        if (!pause && level != null && connection != null) {
            postTickClient()
        }
    }

    internal fun wrapUseItemOn(
        gameMode: MultiPlayerGameMode,
        player: LocalPlayer,
        level: ClientLevel,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult,
        operation: Operation<InteractionResult>,
    ): InteractionResult {
        val result = operation.call(gameMode, player, level, interactionHand, blockHitResult)
        if (!result.consumesAction()) {
            // Consider a train relocation
            val contraptionsInLevel = ContraptionHandler.loadedContraptions.get(level).values
            for (contraptionEntityRef: WeakReference<AbstractContraptionEntity> in contraptionsInLevel) {
                val rayInputs = ContraptionHandlerClient.getRayInputs(player)
                val origin = rayInputs.first
                val target = rayInputs.second
                val aabb = AABB(origin, target).inflate(16.0)
                val contraptionEntity = contraptionEntityRef.get() ?: continue
                if (!contraptionEntity.boundingBox.intersects(aabb))
                    continue

                val rayTraceResult =
                    ContraptionHandlerClient.rayTraceContraption(origin, target, contraptionEntity) ?: continue
                val pos = rayTraceResult.blockPos

                // Effectively do ContraptionHandlerClient.handleSpecialInteractions()
                if (AllItems.WRENCH.isIn(player.getItemInHand(interactionHand)) && contraptionEntity is CarriageContraptionEntity) {
                    TrainRelocator.carriageWrenched(
                        contraptionEntity.toGlobalVector(VecHelper.getCenterOf(pos), 1.0f),
                        contraptionEntity
                    )
                }
            }
        }
        return result
    }
}
