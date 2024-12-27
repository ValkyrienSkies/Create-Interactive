package org.valkyrienskies.create_interactive.fabric;

import net.fabricmc.api.ModInitializer;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class CreateInteractiveModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();

        CreateInteractiveMod.init();
        CreateInteractiveMod.INSTANCE.getREGISTRATE().register();

        FabricConfigImpl.register();
    }
}
