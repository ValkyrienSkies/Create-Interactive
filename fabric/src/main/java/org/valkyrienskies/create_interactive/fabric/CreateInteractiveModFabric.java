package org.valkyrienskies.create_interactive.fabric;

import com.simibubi.create.foundation.config.ConfigBase;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.fml.config.ModConfig;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.create_interactive.config.CreateInteractiveConfigs;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

import java.util.Map;

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

        FabricConfigImpl.register();
    }
}
