package org.valkyrienskies.create_interactive.mixinducks;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;

public interface AbstractContraptionEntityDuck {
    void ci$setShadowShipId(final Long shadowShipId);

    Long ci$getShadowShipId();

    AbstractContraptionEntity.ContraptionRotationState ci$getPrevTickRotationState();
}
