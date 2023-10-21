package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedFluidStorage;
import com.simibubi.create.content.contraptions.MountedStorage;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mixin(MountedStorageManager.class)
public abstract class MixinMountedStorageManager {
    @Unique
    private Long ci$shipId = null;
    @Unique
    private final List<Storage<ItemVariant>> ci$externalStorages = new ArrayList<>();
    @Shadow
    protected Contraption.ContraptionInvWrapper inventory;
    @Shadow
    protected Contraption.ContraptionInvWrapper fuelInventory;
    @Shadow
    protected CombinedTankWrapper fluidInventory;
    @Shadow
    protected Map<BlockPos, MountedStorage> storage;
    @Shadow
    protected Map<BlockPos, MountedFluidStorage> fluidStorage;
    @Shadow
    protected abstract Contraption.ContraptionInvWrapper wrapItems(Collection<? extends Storage<ItemVariant>> list, boolean fuel);
    @Shadow
    protected abstract CombinedTankWrapper wrapFluids(Collection<? extends Storage<FluidVariant>> list);

    @Inject(method = "entityTick", at = @At("HEAD"), cancellable = true, remap = false)
    private void preEntityTick(final AbstractContraptionEntity entity, final CallbackInfo ci) {
        ci.cancel();
        if (entity.level.isClientSide) return;

        if (inventory == null) inventory = new Contraption.ContraptionInvWrapper();
        if (fuelInventory == null) fuelInventory = new Contraption.ContraptionInvWrapper();
        if (fluidInventory == null) fluidInventory = new CombinedTankWrapper();

        // Recreate inventories
        final AbstractContraptionEntityDuck duck = (AbstractContraptionEntityDuck) entity;
        ci$shipId = duck.getShadowShipId();
        if (ci$shipId != null) {
            final ServerShip serverShip = VSGameUtilsKt.getShipObjectWorld((ServerLevel) entity.level).getAllShips().getById(ci$shipId);
            final List<Storage<ItemVariant>> inventories = new ArrayList<>();
            final List<Storage<ItemVariant>> fuelInventories = new ArrayList<>();
            // TODO: Support fluid inventories
            serverShip.getActiveChunksSet().forEach((chunkX, chunkZ) -> {
                final LevelChunk chunk = entity.level.getChunk(chunkX, chunkZ);
                for (final BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof ChestBlockEntity chestBlockEntity) {
                        final InventoryStorage newInv = InventoryStorage.of(chestBlockEntity, null);
                        inventories.add(newInv);
                        fuelInventories.add(newInv);
                    } else if (be instanceof ItemVaultBlockEntity itemVaultBlockEntity) {
                        inventories.add(itemVaultBlockEntity.getInventoryOfBlock());
                    } else {
                        final Storage<ItemVariant> newInv = TransferUtil.getItemStorage(be);
                        if (newInv != null) {
                            inventories.add(newInv);
                            fuelInventories.add(newInv);
                        }
                    }
                }
            });
            inventories.addAll(ci$externalStorages);
            fuelInventories.addAll(ci$externalStorages);
            inventory.parts = inventories;
            fuelInventory.parts = fuelInventories;
            // TODO: Fix this!!!!!
            fluidInventory.parts = Collections.EMPTY_LIST;
        } else {
            // Empty storages
            inventory.parts = Collections.EMPTY_LIST;
            fuelInventory.parts = Collections.EMPTY_LIST;
            fluidInventory.parts = Collections.EMPTY_LIST;
        }
    }

    @Inject(method = "createHandlers", at = @At("HEAD"), cancellable = true, remap = false)
    private void preCreateHandlers(final CallbackInfo ci) {
        ci.cancel();
        // Empty storages
        inventory = new Contraption.ContraptionInvWrapper();
        fuelInventory = new Contraption.ContraptionInvWrapper();
        // TODO: Fix this!!!!!
        fluidInventory = new CombinedTankWrapper();
    }

    @Inject(method = "addBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAddBlock(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "read", at = @At("HEAD"), cancellable = true, remap = false)
    private void preRead(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "bindTanks", at = @At("HEAD"), cancellable = true, remap = false)
    private void preBindTanks(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "write", at = @At("HEAD"), cancellable = true, remap = false)
    private void preWrite(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "removeStorageFromWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private void preRemoveStorageFromWorld(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "addStorageToWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAddStorageToWorld(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "clear", at = @At("HEAD"), cancellable = true, remap = false)
    private void preClear(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "updateContainedFluid", at = @At("HEAD"), cancellable = true, remap = false)
    private void preUpdateContainedFluid(final CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "attachExternal", at = @At("HEAD"), cancellable = true, remap = false)
    private void preAttachExternal(final Storage<ItemVariant> externalStorage, final CallbackInfo ci) {
        ci.cancel();
        if (externalStorage == null) return;
        ci$externalStorages.add(externalStorage);
    }

    @Inject(method = "handlePlayerStorageInteraction", at = @At("HEAD"), cancellable = true, remap = false)
    private void preHandlePlayerStorageInteraction(final Contraption contraption, final Player player, final BlockPos localPos, final CallbackInfoReturnable<Boolean> cir) {
        // Disable this entirely
        cir.setReturnValue(false);
    }
}
