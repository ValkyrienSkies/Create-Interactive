package org.valkyrienskies.create_interactive.mixinducks;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface AbstractContraptionEntityDuck {
    void ci$setShadowShipId(final Long shadowShipId);

    @Nullable
    List<Pair<BlockPos, BlockPos>> ci$getPropagators();

    @Nullable
    Long ci$getShadowShipId();

    @NotNull
    AbstractContraptionEntity.ContraptionRotationState ci$getPrevTickRotationState();
}
