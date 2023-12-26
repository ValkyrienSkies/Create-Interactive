package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.track.ITrackBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.create_interactive.mixin_logic.MixinITrackBlockLogic;

import java.util.List;

@Mixin(ITrackBlock.class)
public interface MixinITrackBlock {
    @Redirect(method = "getNearestTrackAxis", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/track/ITrackBlock;getTrackAxes(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/List;"))
    private List<Vec3> modifyGetNearestTrackAxis(ITrackBlock instance, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
        return MixinITrackBlockLogic.INSTANCE.modifyGetNearestTrackAxis$create_interactive(
            instance, blockGetter, blockPos, blockState
        );
    }
}
