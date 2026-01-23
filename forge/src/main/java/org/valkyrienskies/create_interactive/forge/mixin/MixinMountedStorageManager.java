package org.valkyrienskies.create_interactive.forge.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.forge.InteractiveMountedItemStorage;
import org.valkyrienskies.create_interactive.forge.mixin_logic.mixin.MixinMountedStorageManagerLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(MountedStorageManager.class)
public abstract class MixinMountedStorageManager {
    @Unique
    private Long ci$shipId = null;
    @Unique
    private Map<BlockPos, InteractiveMountedItemStorage> ci$externalStorages;

    @Shadow
    protected CombinedInvWrapper allItems;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void postInit(final CallbackInfo ci) {
        ci$externalStorages = new HashMap<>();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void preEntityTick(final AbstractContraptionEntity entity, final CallbackInfo ci) {
        ci$shipId = ((AbstractContraptionEntityDuck) entity).ci$getShadowShipId();
        if (check()) {
            ci.cancel();
            if (entity.level().isClientSide) return;

            Map<BlockPos, InteractiveMountedItemStorage> itemStorages = new HashMap<>();
            Map<BlockPos, MountedFluidStorage> fluidStorages = new HashMap<>();

            // Recreate inventories
            MixinMountedStorageManagerLogic.INSTANCE.preEntityTick$create_interactive(
                    entity, ci$shipId, itemStorages, fluidStorages
            );
            ci$externalStorages = itemStorages;
        }
    }

    @Inject(method = "addBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAddBlock(final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
        }
    }

    @Inject(method = "read", at = @At("HEAD"), cancellable = true, remap = false)
    private void preRead(final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
        }
    }

    @Inject(method = "addStorage(Lcom/simibubi/create/api/contraption/storage/fluid/MountedFluidStorage;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void preBindTanks(final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
        }
    }

    @Inject(method = "write", at = @At("HEAD"), cancellable = true, remap = false)
    private void preWrite(final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
        }
    }

    @Inject(method = "unmount", at = @At("HEAD"), cancellable = true, remap = false)
    private void preRemoveStorageFromWorld(final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
        }
    }

    @Inject(method = "addStorage(Lcom/simibubi/create/api/contraption/storage/item/MountedItemStorage;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAddStorageToWorld(final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
        }
    }

    @Inject(method = "attachExternal", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAttachExternal(IItemHandlerModifiable externalStorage, CallbackInfo ci) {
        if (check()) {
            ci.cancel();
            if (externalStorage == null) return;
            IItemHandlerModifiable[] all = ci$externalStorages.values().toArray(new IItemHandlerModifiable[ci$externalStorages.size() + 1]);
            all[ci$externalStorages.size()] = (externalStorage);
            this.allItems = new CombinedInvWrapper(all);
        }
    }

    @Inject(
            method = "getAllItems",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void redirectGetAllItems(CallbackInfoReturnable<CombinedInvWrapper> cir){
        if(check()){
            IItemHandlerModifiable[] all = ci$externalStorages.values().toArray(new IItemHandlerModifiable[0]);
            cir.setReturnValue(new CombinedInvWrapper(all));
        }
    }

    @Inject(method = "handlePlayerStorageInteraction", at = @At("HEAD"), cancellable = true, remap = false)
    private void preHandlePlayerStorageInteraction(final Contraption contraption, final Player player, final BlockPos localPos, final CallbackInfoReturnable<Boolean> cir) {
        // Disable this entirely
        if (check()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean check() {
        return ci$shipId != null;
    }
}
