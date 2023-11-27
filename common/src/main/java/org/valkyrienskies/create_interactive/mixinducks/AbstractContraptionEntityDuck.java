package org.valkyrienskies.create_interactive.mixinducks;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import kotlin.Pair;
import net.minecraft.core.BlockPos;

import java.util.List;

public interface AbstractContraptionEntityDuck {
    void ci$setShadowShipId(final Long shadowShipId);

    List<Pair<BlockPos, BlockPos>> ci$getPropegators();

    Long ci$getShadowShipId();

    AbstractContraptionEntity.ContraptionRotationState ci$getPrevTickRotationState();
}
