package org.valkyrienskies.create_interactive.fabric.mixin;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.fabric.InteractiveMountedItemStorage;
import org.valkyrienskies.create_interactive.fabric.mixin_logic.mixin.MixinMountedStorageManagerLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(MountedStorageManager.class)
public abstract class MixinMountedStorageManager {
    @Unique
    private Long ci$shipId = null;
    @Unique
    private Map<BlockPos, InteractiveMountedItemStorage> ci$externalStorages;

    @Shadow
    protected CombinedSlottedStorage<ItemVariant, ? extends SlottedStorage<ItemVariant>> allItems;

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

    @Inject(method = "addStorage(Lcom/simibubi/create/api/contraption/storage/fluid/MountedFluidStorage;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true)
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

    @Inject(method = "addStorage(Lcom/simibubi/create/api/contraption/storage/item/MountedItemStorage;Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true)
    private void preAddStorageToWorld(final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
        }
    }

    @Inject(method = "attachExternal", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAttachExternal(SlottedStorage<ItemVariant> externalStorage, CallbackInfo ci) {
        if (check()) {
            ci.cancel();
            if (externalStorage == null) return;
            List<SlottedStorage<ItemVariant>> all = new ArrayList<>(ci$externalStorages.size() + 1);
            all.addAll(this.ci$externalStorages.values());
            all.add(externalStorage);
            this.allItems = new CombinedSlottedStorage<>(all);
        }
    }

    @Inject(
            method = "getAllItems",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void redirectGetAllItems(CallbackInfoReturnable<CombinedSlottedStorage<ItemVariant, ? extends SlottedStorage<ItemVariant>>> cir){
        if(check()){
            List<SlottedStorage<ItemVariant>> all = new ArrayList<>(ci$externalStorages.size() + 1);
            all.addAll(this.ci$externalStorages.values());
            LogUtils.getLogger().info("storages");
            ci$externalStorages.values().forEach(
                    (storage) -> {
                        LogUtils.getLogger().info("    {}", storage);
                    }
            );
            cir.setReturnValue(new CombinedSlottedStorage<>(all));
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
