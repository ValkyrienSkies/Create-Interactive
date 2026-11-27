package org.valkyrienskies.create_interactive.fabric.mixin_logic

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import net.minecraft.client.Minecraft
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.mod.common.isBlockInShipyard

internal object MixinContraptionHandlerClientLogic {
    internal fun wrapGetRayInputs(instance: Vec3, target: Vec3, distanceTo: Operation<Double>): Double {
        val originalDistanceTo = distanceTo.call(instance, target)
        val hitResult = Minecraft.getInstance().hitResult
        if (hitResult is BlockHitResult) {
            val level: Level? = Minecraft.getInstance().level
            // Ship blocks live in the shipyard. Depending on the VS version the hit location is in world or shipyard
            // space, so only the block position is a reliable signal.
            if (level != null && level.isBlockInShipyard(hitResult.blockPos)) {
                // Increase the distance we raytrace contraptions to ensure we hit the contraption instead
                return originalDistanceTo + 0.1
            }
        }
        return originalDistanceTo
    }

    internal fun preHandleSpecialInteractions(cir: CallbackInfoReturnable<Boolean>) {
        val hitResult = Minecraft.getInstance().hitResult
        if (hitResult != null && hitResult.type == HitResult.Type.BLOCK) {
            // Don't handle train interactions if the hit result was a block, we'll handle these later
            cir.setReturnValue(false)
        }
    }
}
