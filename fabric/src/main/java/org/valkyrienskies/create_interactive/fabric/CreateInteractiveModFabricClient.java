package org.valkyrienskies.create_interactive.fabric;

import net.fabricmc.api.ClientModInitializer;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;

public class CreateInteractiveModFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CreateInteractiveMod.initClient();
    }
}
