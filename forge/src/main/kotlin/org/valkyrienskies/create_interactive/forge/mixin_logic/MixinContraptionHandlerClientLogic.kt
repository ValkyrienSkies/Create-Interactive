package org.valkyrienskies.create_interactive.forge.mixin_logic

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import net.minecraft.client.Minecraft
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.create_interactive.VS2KotlinHelper

internal object MixinContraptionHandlerClientLogic {
    internal fun wrapGetRayInputs(instance: Vec3, target: Vec3, distanceTo: Operation<Double>): Double {
        val originalDistanceTo = distanceTo.call(instance, target)
        val hitResult = Minecraft.getInstance().hitResult
        if (hitResult is BlockHitResult) {
            val level: Level? = Minecraft.getInstance().level
            if (level != null && VS2KotlinHelper.isBlockInShipyard(level, hitResult.blockPos) && hitResult.getLocation()
                .distanceToSqr(
                    hitResult.blockPos.x.toDouble(),
                    hitResult.blockPos.y.toDouble(),
                    hitResult.blockPos.z.toDouble()
                ) > 1e6
            ) {
                // If location doesn't match the position then assume this is on a ship, so increase the distance we raytrace contraptions to ensure we hit the contraption instead
                return originalDistanceTo + 0.1
            }
        }
        return originalDistanceTo
    }
}
