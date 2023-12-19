package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainMigration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Train.class)
public interface TrainAccessor {
    @Accessor("migratingPoints")
    List<TrainMigration> getMigratingPoints();
}
