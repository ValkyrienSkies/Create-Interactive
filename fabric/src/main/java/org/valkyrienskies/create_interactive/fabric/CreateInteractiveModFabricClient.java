package org.valkyrienskies.create_interactive.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.create_interactive.GameContent;

public class CreateInteractiveModFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CreateInteractiveMod.initClient();
        BlockRenderLayerMap.INSTANCE.putBlocks( RenderType.cutout(), GameContent.INSTANCE.getINTERACT_ME().get(), GameContent.INSTANCE.getINTERACT_ME_NOT().get());
    }
}
