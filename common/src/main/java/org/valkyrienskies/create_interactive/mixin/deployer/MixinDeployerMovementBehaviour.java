package org.valkyrienskies.create_interactive.mixin.deployer;

import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.create_interactive.CreateInteractiveUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.lang.reflect.Field;

/**
 * Fix DeployerMovementBehaviour
 */
@Mixin(DeployerMovementBehaviour.class)
public class MixinDeployerMovementBehaviour {
    @Unique
    private Field ci$modeField;

    @Unique
    private DeployerBlockEntity ci$getBlockEntity(final MovementContext context) {
        final Ship ship = CreateInteractiveUtil.INSTANCE.getShipForMovementContext(context);
        if (ship == null) return null;
        final Vector3ic shipCenter = ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(context.world), new Vector3i());
        final BlockPos blockPos = context.localPos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z());
        final BlockEntity blockEntity = context.world.getBlockEntity(blockPos);
        if (!(blockEntity instanceof DeployerBlockEntity deployerBlockEntity)) return null;
        return deployerBlockEntity;
    }

    @Inject(method = "getPlayer", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetPlayer(final MovementContext context, final CallbackInfoReturnable<DeployerFakePlayer> cir) {
        final DeployerBlockEntity deployerBlockEntity = ci$getBlockEntity(context);
        if (deployerBlockEntity == null) return;
        cir.setReturnValue(deployerBlockEntity.getPlayer());
    }

    @Inject(method = "getFilter", at = @At("HEAD"), cancellable = true, remap = false)
    private void preGetFilter(final MovementContext context, final CallbackInfoReturnable<ItemStack> cir) {
        final DeployerBlockEntity deployerBlockEntity = ci$getBlockEntity(context);
        if (deployerBlockEntity == null) return;
        cir.setReturnValue(((DeployerBlockEntityAccessor) deployerBlockEntity).getFiltering().getFilter());
    }

    @Inject(method = "getMode", at = @At("HEAD"), cancellable = true, remap = false)
    private void getMode(final MovementContext context, final CallbackInfoReturnable<Object> cir) throws NoSuchFieldException, IllegalAccessException {
        final DeployerBlockEntity deployerBlockEntity = ci$getBlockEntity(context);
        if (deployerBlockEntity == null) return;
        if (ci$modeField == null) {
            ci$modeField = DeployerBlockEntity.class.getDeclaredField("mode");
            ci$modeField.setAccessible(true);
        }
        cir.setReturnValue(ci$modeField.get(deployerBlockEntity));
    }
}
