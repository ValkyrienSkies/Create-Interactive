package org.valkyrienskies.create_interactive.fabric;

import com.simibubi.create.foundation.config.ConfigBase;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.create_interactive.config.CreateInteractiveConfigs;

import java.util.Map;

public class FabricConfigImpl {
    public static void register() {
        CreateInteractiveConfigs.registerCommon();

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CreateInteractiveConfigs.CONFIGS.entrySet())
            ForgeConfigRegistry.INSTANCE.register(CreateInteractiveMod.MOD_ID, pair.getKey(), pair.getValue().specification);

        ModConfigEvents.loading(CreateInteractiveMod.MOD_ID).register(CreateInteractiveConfigs::onLoad);
        ModConfigEvents.reloading(CreateInteractiveMod.MOD_ID).register(CreateInteractiveConfigs::onReload);
    }
}
