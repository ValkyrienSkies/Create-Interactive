package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.track.ITrackBlock;
import net.createmod.catnip.data.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.valkyrienskies.create_interactive.mixin_logic.MixinITrackBlockLogic;

@Mixin(ITrackBlock.class)
public interface MixinITrackBlock {
    /**
     * @author Triode
     * @reason We cannot do @Inject on interface default methods (see <a href="https://github.com/SpongePowered/Mixin/issues/421#issuecomment-915109038">this issue</a>)
     */
    @Overwrite
    default Pair<Vec3, Direction.AxisDirection> getNearestTrackAxis(
        final BlockGetter world,
        final BlockPos pos,
        final BlockState state,
        final Vec3 lookVec
    ) {
        return MixinITrackBlockLogic.INSTANCE.overwriteGetNearestTrackAxis$create_interactive(
            ITrackBlock.class.cast(this), world, pos, state, lookVec
        );
    }
}
