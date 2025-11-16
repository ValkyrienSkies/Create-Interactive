package org.valkyrienskies.create_interactive.fabric;

import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.create_interactive.GameContent;
import org.valkyrienskies.create_interactive.content.ponders.InteractivePonderRegistry;

public class CreateInteractiveModFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        CreateInteractiveMod.initClient();
        PonderIndex.addPlugin(new InteractivePonderRegistry());
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), GameContent.INTERACT_ME.get(), GameContent.INTERACT_ME_NOT.get());
    }
}
