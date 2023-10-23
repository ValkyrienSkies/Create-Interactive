package org.valkyrienskies.create_interactive.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(ContraptionHandlerClient.class)
public class MixinContraptionHandlerClient {
    /**
     * This makes contraptions prioritized over ships during raytracing, which allows players to interact with contraptions
     */
    @WrapOperation(
        method = "getRayInputs",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D"
        )
    )
    private static double wrapGetRayInputs(final Vec3 instance, final Vec3 target, final Operation<Double> distanceTo) {
        final double originalDistanceTo = distanceTo.call(instance, target);
        final HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof BlockHitResult blockHitResult) {
            final Level level = Minecraft.getInstance().level;
            if (level != null && VSGameUtilsKt.isBlockInShipyard(level, ((BlockHitResult) hitResult).getBlockPos()) && blockHitResult.getLocation().distanceToSqr(blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().getY(), blockHitResult.getBlockPos().getZ()) > 1e6) {
                // If location doesn't match the position then assume this is on a ship, so increase the distance we raytrace contraptions to ensure we hit the contraption instead
                return originalDistanceTo + 0.1;
            }
        }
        return originalDistanceTo;
    }
}
