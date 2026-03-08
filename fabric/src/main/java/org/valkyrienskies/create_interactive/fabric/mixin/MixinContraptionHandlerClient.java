package org.valkyrienskies.create_interactive.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.fabric.mixin_logic.MixinContraptionHandlerClientLogic;

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
    @WrapOperation(
            method = "rightClickingOnContraptionsGetsHandledLocally",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;handlePlayerInteraction(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/InteractionHand;)Z")
    )
    private static boolean wrapHandleInteraction(AbstractContraptionEntity entity, Player player, BlockPos blockPos, Direction direction, InteractionHand hand, Operation<Boolean> original, @Share("success") LocalRef<Boolean> success){
        boolean result = original.call(entity, player, blockPos, direction, hand);
        success.set(result);
        return result;
    }

    @WrapMethod(
            method = "rightClickingOnContraptionsGetsHandledLocally"
    )
    private static InteractionResult wrapInteractionResult(Minecraft mc, HitResult result, InteractionHand hand, Operation<InteractionResult> original, @Share("success") LocalRef<Boolean> success) {
        InteractionResult interactionResult = original.call(mc, result, hand);
        if (interactionResult.equals(InteractionResult.FAIL) && !success.get()) {
            return InteractionResult.PASS;
        } else return interactionResult;
    }

    /**
     * Cancel handling train interactions if the hit result was a block, we'll handle them later
     */
    @Inject(method = "handleSpecialInteractions", at = @At("HEAD"), cancellable = true)
    private static void preHandleSpecialInteractions(final CallbackInfoReturnable<Boolean> cir) {
        MixinContraptionHandlerClientLogic.INSTANCE.preHandleSpecialInteractions$create_interactive(cir);
    }
}
