package org.valkyrienskies.create_interactive.forge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
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

    @WrapOperation(
            method = "rightClickingOnContraptionsGetsHandledLocally",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/contraptions/AbstractContraptionEntity;handlePlayerInteraction(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/InteractionHand;)Z"),
            remap = false
    )
    private static boolean interactionSuccess(AbstractContraptionEntity instance, Player entry, BlockPos transformedVector, Direction direction, InteractionHand player, Operation<Boolean> original, @Local(argsOnly = true) InputEvent.InteractionKeyMappingTriggered event){
        if (original.call(instance, entry, transformedVector, direction, player)) {
            event.setCanceled(true);
            return true;
        } return false;
    }

    @Inject(
            method = "rightClickingOnContraptionsGetsHandledLocally",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/event/InputEvent$InteractionKeyMappingTriggered;setCanceled(Z)V", remap = true),
            remap = false,
            cancellable = true
    )
    private static void interactionPassed(InputEvent.InteractionKeyMappingTriggered event, CallbackInfo ci){
        ci.cancel();
    }

    /**
     * Cancel handling train interactions if the hit result was a block, we'll handle them later
     */
    @Inject(method = "handleSpecialInteractions", at = @At("HEAD"), cancellable = true, remap = false)
    private static void preHandleSpecialInteractions(final CallbackInfoReturnable<Boolean> cir) {
        final HitResult hitResult = Minecraft.getInstance().hitResult;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            // Don't handle train interactions if the hit result was a block, we'll handle these later
            cir.setReturnValue(false);
        }
    }
}
