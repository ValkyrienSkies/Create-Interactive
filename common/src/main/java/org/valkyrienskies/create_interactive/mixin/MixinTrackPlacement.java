package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackPlacement;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.create_interactive.mixin_logic.MixinTrackPlacementLogic;

@Mixin(TrackPlacement.class)
public class MixinTrackPlacement {
    // TODO: Replace this @WrapOperation
    /**
     * Fix placing rails on rotated ships
     */
    @Redirect(method = "tryConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 redirectTryConnectInvokeGetLookAngle(
        final Player instance,
        final Level level,
        final Player player,
        final BlockPos pos2,
        final BlockState state2,
        final ItemStack stack,
        final boolean girder,
        final boolean maximiseTurn
    ) {
        return MixinTrackPlacementLogic.INSTANCE.redirectTryConnectInvokeGetLookAngle$create_interactive(
            instance, level, player, pos2, state2, stack, girder, maximiseTurn
        );
    }

    // TODO: Replace this @WrapOperation
    /**
     * Fix placing rails on rotated ships part 2. Because of our mixin to getNearestTrackAxis we need to re-invoke without transforming the player look vector
     */
    @Redirect(method = "tryConnect", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/ITrackBlock;getNearestTrackAxis(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;)Lcom/simibubi/create/foundation/utility/Pair;"))
    private static Pair<Vec3, Direction.AxisDirection> redirectTryConnectInvokeGetNearestTrackAxis(
        final ITrackBlock iTrackBlock,
        final BlockGetter world,
        final BlockPos pos,
        final BlockState state,
        final Vec3 lookVec,
        final Level level,
        final Player player,
        final BlockPos pos2,
        final BlockState state2,
        final ItemStack stack,
        final boolean girder,
        final boolean maximiseTurn
    ) {
        return MixinTrackPlacementLogic.INSTANCE.redirectTryConnectInvokeGetNearestTrackAxis$create_interactive(iTrackBlock, world, pos, state, lookVec, level, player, pos2, state2, stack, girder, maximiseTurn);
    }
}
