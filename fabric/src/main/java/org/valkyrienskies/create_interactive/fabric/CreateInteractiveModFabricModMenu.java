package org.valkyrienskies.create_interactive.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.create_interactive.CreateInteractiveConfig;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

public class CreateInteractiveModFabricModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> VSClothConfig.createConfigScreenFor(
            parent,
            VSConfigClass.Companion.getRegisteredConfig(CreateInteractiveConfig.class)
        );
    }
}
