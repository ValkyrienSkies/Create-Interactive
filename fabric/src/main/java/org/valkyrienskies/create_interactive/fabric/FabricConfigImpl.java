package org.valkyrienskies.create_interactive.fabric;

import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.create_interactive.config.CreateInteractiveConfigs;

import java.util.Map;

public class FabricConfigImpl {
    public static void register() {
        CreateInteractiveConfigs.registerCommon();

        for (Map.Entry<ModConfig.Type, ConfigBase> pair : CreateInteractiveConfigs.CONFIGS.entrySet())
            ModLoadingContext.registerConfig(CreateInteractiveMod.MOD_ID, pair.getKey(), pair.getValue().specification);

        ModConfigEvent.LOADING.register(CreateInteractiveConfigs::onLoad);
        ModConfigEvent.RELOADING.register(CreateInteractiveConfigs::onReload);
    }
}
