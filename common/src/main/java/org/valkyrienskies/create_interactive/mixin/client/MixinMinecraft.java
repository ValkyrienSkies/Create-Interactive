package org.valkyrienskies.create_interactive.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinMinecraftLogic;
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider;

// Set priority to 1001 to run these after VS2 mixins
@Mixin(value = Minecraft.class, priority = 1001)
public abstract class MixinMinecraft implements IShipObjectWorldClientProvider {
    @Shadow
    private boolean pause;
    @Shadow
    public ClientLevel level;

    @Shadow
    public abstract ClientPacketListener getConnection();

    @Inject(
        method = "tick",
        at = @At("RETURN")
    )
    public void postTick(final CallbackInfo ci) {
        MixinMinecraftLogic.INSTANCE.postTick$create_interactive(pause, level, getConnection());
    }

    /**
     * Fix train wrenching by doing train wrenching after block interactions
     */
    @WrapOperation(
        method = "startUseItem",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"
        )
    )
    private InteractionResult wrapUseItemOn(
        final MultiPlayerGameMode gameMode,
        final LocalPlayer player,
        final InteractionHand interactionHand,
        final BlockHitResult blockHitResult,
        final Operation<InteractionResult> operation
    ) {
        return MixinMinecraftLogic.INSTANCE.wrapUseItemOn$create_interactive(
            gameMode, player, interactionHand, blockHitResult, operation
        );
    }
}
