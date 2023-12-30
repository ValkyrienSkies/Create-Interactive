package org.valkyrienskies.create_interactive.forge.mixin;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.valkyrienskies.create_interactive.mixin_logic.MixinTrainLogic;

@Mixin(Train.class)
public class MixinTrain {
    /**
     * @author Triode
     * @reason Don't collide trains with derailed trains
     */
    @Overwrite(remap = false)
    public Pair<Train, Vec3> findCollidingTrain(
        final Level level,
        final Vec3 start,
        final Vec3 end,
        final ResourceKey<Level> dimension
    ) {
        return MixinTrainLogic.INSTANCE.findCollidingTrain(level, start, end, Train.class.cast(this), dimension);
    }
}
