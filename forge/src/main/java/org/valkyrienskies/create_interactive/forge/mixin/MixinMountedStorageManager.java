package org.valkyrienskies.create_interactive.forge.mixin;

import com.google.common.collect.ImmutableMap;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.forge.mixin_logic.mixin.MixinMountedStorageManagerLogic;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Mixin(MountedStorageManager.class)
public abstract class MixinMountedStorageManager {
    @Unique
    private Long ci$shipId = null;
    @Unique
    private List<IItemHandlerModifiable> ci$externalStorages;
    @Shadow
    private ImmutableMap<BlockPos, MountedItemStorage> allItemStorages;
    @Shadow(remap = false)
    protected MountedItemStorageWrapper items;
    @Nullable
    @Shadow(remap = false)
    protected MountedItemStorageWrapper fuelItems;
    @Shadow(remap = false)
    protected MountedFluidStorageWrapper fluids;

    @Shadow
    private Map<BlockPos, MountedFluidStorage> fluidsBuilder;

    @Shadow
    protected abstract boolean isExposed(MountedItemStorage storage);

    @Shadow
    protected abstract boolean canUseForFuel(MountedItemStorage storage);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void postInit(final CallbackInfo ci) {
        ci$externalStorages = new ArrayList<>();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void preEntityTick(final AbstractContraptionEntity entity, final CallbackInfo ci) {
        ci$shipId = ((AbstractContraptionEntityDuck) entity).ci$getShadowShipId();
        if (check()) {
            ci.cancel();
            if (entity.level().isClientSide) return;

            if (items == null) items = new MountedItemStorageWrapper(vs$subMap(this.allItemStorages, this::isExposed));
            if (fuelItems == null) {
                ImmutableMap<BlockPos, MountedItemStorage> fuelMap = vs$subMap(this.allItemStorages, this::canUseForFuel);
                fuelItems = new MountedItemStorageWrapper(fuelMap);
            }
            if (fluids == null) fluids = new MountedFluidStorageWrapper(ImmutableMap.copyOf(this.fluidsBuilder));

            // Recreate inventories

            MixinMountedStorageManagerLogic.INSTANCE.preEntityTick$create_interactive(entity, ci$shipId, ci$externalStorages, items, fuelItems, fluids);
        }
    }

//    @Inject(method = "createHandlers", at = @At("HEAD"), cancellable = true, remap = false)
//    private void preCreateHandlers(final CallbackInfo ci) {
//        if (check()) {
//            ci.cancel();
//            // Empty storages
//            inventory = new Contraption.ContraptionInvWrapper();
//            fuelInventory = new Contraption.ContraptionInvWrapper();
//            fluidInventory = new CombinedTankWrapper();
//        }
//    }

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

    @Inject(method = "reset", at = @At("HEAD"), cancellable = true, remap = false)
    private void preClear(final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
        }
    }

//    @Inject(method = "updateContainedFluid", at = @At("HEAD"), cancellable = true, remap = false)
//    private void preUpdateContainedFluid(final CallbackInfo ci) {
//        if (check()) {
//            ci.cancel();
//        }
//    }

    @Inject(method = "attachExternal", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAttachExternal(final IItemHandlerModifiable externalStorage, final CallbackInfo ci) {
        if (check()) {
            ci.cancel();
            if (externalStorage == null) return;
            ci$externalStorages.add(externalStorage);
        }
    }

    @Inject(method = "handlePlayerStorageInteraction", at = @At("HEAD"), cancellable = true, remap = false)
    private void preHandlePlayerStorageInteraction(final Contraption contraption, final Player player, final BlockPos localPos, final CallbackInfoReturnable<Boolean> cir) {
        if (check()) {
            // Disable this entirely
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
