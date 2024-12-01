package org.valkyrienskies.create_interactive.mixin;

import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TrainMigration;
import com.simibubi.create.content.trains.graph.TrackGraph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Train.class)
public interface TrainAccessor {
    @Accessor(value = "migratingPoints", remap = false)
    List<TrainMigration> getMigratingPoints();

    @Accessor(value = "graph",remap = false)
    void setGraph(TrackGraph graph);

    @Accessor(value = "derailed",remap = false)
    void setDerailed(boolean derailed);
}
