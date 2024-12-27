package org.valkyrienskies.create_interactive.forge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.create_interactive.GameContent;

@Mod(CreateInteractiveMod.MOD_ID)
public class CreateInteractiveModForge {

    public CreateInteractiveModForge() {

        var MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        CreateInteractiveMod.INSTANCE.getREGISTRATE().registerEventListeners(MOD_BUS);

        CreateInteractiveMod.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> CreateInteractiveMod::initClient);

        MOD_BUS.addListener(CreateInteractiveModForge::onClientSetup);

        ForgeConfigImpl.register(ModLoadingContext.get());
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(GameContent.INTERACT_ME.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GameContent.INTERACT_ME_NOT.get(), RenderType.cutout());
    }
}
