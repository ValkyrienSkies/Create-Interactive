package org.valkyrienskies.create_interactive.fabric.mixin;

import com.google.common.collect.ImmutableMap;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage;
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
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
import org.valkyrienskies.create_interactive.fabric.mixin_logic.mixin.MixinMountedStorageManagerLogic;
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
    private List<SlottedStorage<ItemVariant>> ci$externalStorages;
    @Shadow(remap = false)
    protected MountedItemStorageWrapper items;
    @Nullable
    @Shadow(remap = false)
    protected MountedItemStorageWrapper fuelItems;
    @Shadow(remap = false)
    protected MountedFluidStorageWrapper fluids;

    @Shadow
    protected abstract boolean isExposed(MountedItemStorage storage);

    @Shadow
    protected abstract boolean canUseForFuel(MountedItemStorage storage);

    @Shadow
    private ImmutableMap<BlockPos, MountedItemStorage> allItemStorages;

    @Shadow
    protected CombinedSlottedStorage<ItemVariant, ? extends SlottedStorage<ItemVariant>> allItems;

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void postInit(final CallbackInfo ci) {
        ci$externalStorages = new ArrayList<>();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void preEntityTick(final AbstractContraptionEntity entity, final CallbackInfo ci) {
        ci$shipId = ((AbstractContraptionEntityDuck) entity).ci$getShadowShipId();
        if (check()) {
            ci.cancel();
            if (entity.level().isClientSide) return;

            Map<BlockPos, MountedItemStorage> itemStorages = new HashMap<>();
            Map<BlockPos, MountedFluidStorage> fluidStorages = new HashMap<>();

            // Recreate inventories
            MixinMountedStorageManagerLogic.INSTANCE.preEntityTick$create_interactive(
                    entity, ci$shipId, itemStorages, fluidStorages
            );

            allItemStorages = ImmutableMap.copyOf(itemStorages);
            items = new MountedItemStorageWrapper(vs$subMap(itemStorages, this::isExposed));
            fuelItems = new MountedItemStorageWrapper(vs$subMap(itemStorages, this::canUseForFuel));
            fluids = new MountedFluidStorageWrapper(ImmutableMap.copyOf(fluidStorages));
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

//    @Inject(method = "reset", at = @At("HEAD"), cancellable = true, remap = false)
//    private void preClear(final CallbackInfo ci) {
//        if (check()) {
//            ci.cancel();
//        }
//    }
//
//    @Inject(method = "updateContainedFluid", at = @At("HEAD"), cancellable = true, remap = false)
//    private void preUpdateContainedFluid(final CallbackInfo ci) {
//        if (check()) {
//            ci.cancel();
//        }
//    }

    @Inject(method = "attachExternal", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAttachExternal(SlottedStorage<ItemVariant> externalStorage, CallbackInfo ci) {
        if (check()) {
            ci.cancel();
            if (externalStorage == null) return;
            ci$externalStorages.add(externalStorage);
            List<SlottedStorage<ItemVariant>> all = new ArrayList<>(ci$externalStorages.size() + 1);
            all.add(0, this.items);
            all.addAll(this.ci$externalStorages);
            this.allItems = new CombinedSlottedStorage<>(all);
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

    @Unique
    private static <K, V> ImmutableMap<K, V> vs$subMap(Map<K, V> map, Predicate<V> predicate) {
        ImmutableMap.Builder<K, V> builder = ImmutableMap.builder();
        map.forEach((key, value) -> {
            if (predicate.test(value)) {
                builder.put(key, value);
            }
        });
        return builder.build();
    }
}
