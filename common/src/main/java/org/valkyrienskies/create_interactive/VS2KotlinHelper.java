package org.valkyrienskies.create_interactive;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

/**
 * This only exists because forge gradle is broken and I can't be bothered to fix it
 */
public class VS2KotlinHelper {
    public static boolean isBlockInShipyard(final Level level, final BlockPos pos) {
        return VSGameUtilsKt.isBlockInShipyard(level, pos);
    }

    public static ServerShip getShipById(final ServerLevel serverLevel, final long id) {
        return VSGameUtilsKt.getShipObjectWorld(serverLevel).getAllShips().getById(id);
    }

    public static Screen createConfigScreenFor(final Screen parent, final VSConfigClass... configClasses) {
        return VSClothConfig.createConfigScreenFor(parent, configClasses);
    }
}
