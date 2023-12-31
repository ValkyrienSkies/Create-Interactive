package org.valkyrienskies.create_interactive.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class CreateInteractiveModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();

        CreateInteractiveMod.init();
        CreateInteractiveMod.INSTANCE.getREGISTRATE().register();

        Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            CreateInteractiveMod.INSTANCE.getINTERACTIVE_CREATIVE_TAB(),
            CreateInteractiveMod.INSTANCE.createCreativeTab()
        );
    }
}
