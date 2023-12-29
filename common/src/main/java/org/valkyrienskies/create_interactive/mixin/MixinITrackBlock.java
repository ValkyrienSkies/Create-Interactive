package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.create_interactive.mixin_logic.MixinITrackBlockLogic;

@Mixin(ITrackBlock.class)
public interface MixinITrackBlock {
    @Inject(method = "getNearestTrackAxis", at = @At("HEAD"), cancellable = true)
    private void preGetNearestTrackAxis(
        final BlockGetter world,
        final BlockPos pos,
        final BlockState state,
        final Vec3 lookVec,
        final CallbackInfoReturnable<Pair<Vec3, Direction.AxisDirection>> cir
    ) {
        MixinITrackBlockLogic.INSTANCE.preGetNearestTrackAxis$create_interactive(
            ITrackBlock.class.cast(this), world, pos, state, lookVec, cir
        );
    }
}
