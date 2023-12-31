package org.valkyrienskies.create_interactive.forge;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;

/**
 * Why does this exist? Because compiling kotlin is bugged for forge. :/
 */
public class CreateInteractiveModForgeHelper {
    public static void registerRegistrate(final IEventBus modBus) {
        CreateInteractiveMod.INSTANCE.getREGISTRATE().registerEventListeners(modBus);
    }

    public static CreativeModeTab createCreativeTab() {
        return CreateInteractiveMod.INSTANCE.createCreativeTab();
    }
}
