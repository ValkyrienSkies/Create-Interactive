package org.valkyrienskies.create_interactive.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.create_interactive.forge.mixin_logic.MixinContraptionHandlerClientLogic;

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
        return MixinContraptionHandlerClientLogic.INSTANCE.wrapGetRayInputs$create_interactive(instance, target, distanceTo);
    }
}
