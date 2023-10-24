package org.valkyrienskies.create_interactive.forge.mixin;

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

import java.util.ArrayList;
import java.util.List;

@Mixin(MountedStorageManager.class)
public abstract class MixinMountedStorageManager {
    @Unique
    private Long ci$shipId = null;
    @Unique
    private List<IItemHandlerModifiable> ci$externalStorages;
    @Shadow
    protected Contraption.ContraptionInvWrapper inventory;
    @Shadow
    protected Contraption.ContraptionInvWrapper fuelInventory;
    @Shadow
    protected CombinedTankWrapper fluidInventory;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void postInit(final CallbackInfo ci) {
        ci$externalStorages = new ArrayList<>();
    }

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

        MixinMountedStorageManagerLogic.INSTANCE.preEntityTick$create_interactive(entity, ci$shipId, ci$externalStorages, inventory, fuelInventory, fluidInventory);
    }

    @Inject(method = "createHandlers", at = @At("HEAD"), cancellable = true, remap = false)
    private void preCreateHandlers(final CallbackInfo ci) {
        ci.cancel();
        // Empty storages
        inventory = new Contraption.ContraptionInvWrapper();
        fuelInventory = new Contraption.ContraptionInvWrapper();
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
    private void preAttachExternal(final IItemHandlerModifiable externalStorage, final CallbackInfo ci) {
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
