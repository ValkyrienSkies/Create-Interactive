package org.valkyrienskies.create_interactive.mixinducks;

import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;

public interface CarriageDuck {
    /**
     * Pass in shadowShipId, so we can initialize the Contraption shipId if it's not set already
     */
    boolean ci$doesCarriageEntityControlShip(CarriageContraptionEntity entity, final Long shadowShipId);
}
