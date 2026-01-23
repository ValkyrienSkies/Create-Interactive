package org.valkyrienskies.create_interactive.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlock;
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsBlockEntity;
import com.simibubi.create.content.contraptions.elevator.ElevatorContraption;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(value = FilteringRenderer.class, remap = false)
public abstract class MixinFilteringRenderer {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;getAllBehaviours()Ljava/util/Collection;"), cancellable = true)
    private static void preTick(CallbackInfo ci, @Local(ordinal = 0) BlockState blockState, @Local(ordinal = 0) ClientLevel world, @Local(ordinal = 0) BlockPos pos) {
        if (blockState.getBlock() instanceof ContraptionControlsBlock) {
            final Ship ship = VSGameUtilsKt.getShipManagingPos(world, pos);
            if (ship != null) {
                final AbstractContraptionEntity contraption = CreateInteractiveUtil.INSTANCE.getContraptionEntityForShip(ship.getId(), world.isClientSide());
                if (contraption != null && contraption.getContraption() instanceof ElevatorContraption) {
                    ci.cancel();
                }
            }
        }
    }

    @Inject(
            method = "renderOnBlockEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/foundation/blockEntity/SmartBlockEntity;getAllBehaviours()Ljava/util/Collection;"
            ),
            cancellable = true
    )
    private static void preRenderOnBlockEntity(SmartBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay, CallbackInfo ci) {
        if (be instanceof ContraptionControlsBlockEntity) {
            final Level level = be.getLevel();
            final Ship ship = VSGameUtilsKt.getShipManagingPos(level, be.getBlockPos());
            if (ship != null) {
                final AbstractContraptionEntity contraption = CreateInteractiveUtil.INSTANCE.getContraptionEntityForShip(ship.getId(), level.isClientSide());
                if (contraption != null && contraption.getContraption() instanceof ElevatorContraption) {
                    ci.cancel();
                }
            }
        }
    }
}
