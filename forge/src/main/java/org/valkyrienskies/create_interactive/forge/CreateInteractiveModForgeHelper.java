package org.valkyrienskies.create_interactive.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;

/**
 * Why does this exist? Because compiling kotlin is bugged for forge. :/
 */
public class CreateInteractiveModForgeHelper {
    public static void registerRegistrate(final IEventBus modBus) {
        CreateInteractiveMod.INSTANCE.getREGISTRATE().registerEventListeners(modBus);
    }
}
