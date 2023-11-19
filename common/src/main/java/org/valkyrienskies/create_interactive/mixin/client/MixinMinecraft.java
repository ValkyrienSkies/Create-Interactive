package org.valkyrienskies.create_interactive.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ContraptionHandler;
import com.simibubi.create.content.contraptions.ContraptionHandlerClient;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.TrainRelocator;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinMinecraftLogic;
import org.valkyrienskies.mod.common.IShipObjectWorldClientProvider;

import java.lang.ref.WeakReference;

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
        MixinMinecraftLogic.INSTANCE.postTick$create_interactive(pause, level, getConnection(), ci);
    }

    /**
     * Fix train wrenching by doing train wrenching after block interactions
     */
    @WrapOperation(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;useItemOn(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
    private InteractionResult wrapUseItemOn(
        final MultiPlayerGameMode gameMode,
        final LocalPlayer player,
        final ClientLevel level,
        final InteractionHand interactionHand,
        final BlockHitResult blockhitresult,
        final Operation<InteractionResult> operation
    ) {
        final InteractionResult result = operation.call(gameMode, player, level, interactionHand, blockhitresult);
        if (!result.consumesAction()) {
            // Consider a train relocation
            for (final WeakReference<AbstractContraptionEntity> contraptionEntityRef : ContraptionHandler.loadedContraptions.get(level).values()) {
                final Couple<Vec3> rayInputs = ContraptionHandlerClient.getRayInputs(player);
                final Vec3 origin = rayInputs.getFirst();
                final Vec3 target = rayInputs.getSecond();
                final AABB aabb = new AABB(origin, target).inflate(16);
                final AbstractContraptionEntity contraptionEntity = contraptionEntityRef.get();
                if (contraptionEntity == null)
                    continue;
                if (!contraptionEntity.getBoundingBox()
                    .intersects(aabb))
                    continue;

                final BlockHitResult rayTraceResult = ContraptionHandlerClient.rayTraceContraption(origin, target, contraptionEntity);
                if (rayTraceResult == null)
                    continue;

                final BlockPos pos = rayTraceResult.getBlockPos();

                // Effectively do ContraptionHandlerClient.handleSpecialInteractions()
                if (AllItems.WRENCH.isIn(player.getItemInHand(interactionHand)) && contraptionEntity instanceof CarriageContraptionEntity car) {
                    TrainRelocator.carriageWrenched(car.toGlobalVector(VecHelper.getCenterOf(pos), 1), car);
                }
            }
        }
        return result;
    }
}
