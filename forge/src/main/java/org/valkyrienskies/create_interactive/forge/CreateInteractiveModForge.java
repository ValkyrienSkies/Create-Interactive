package org.valkyrienskies.create_interactive.forge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.create_interactive.GameContent;

@Mod(CreateInteractiveMod.MOD_ID)
public class CreateInteractiveModForge {

    public CreateInteractiveModForge() {
        // So we can @SubscribeEvent in this class
        MinecraftForge.EVENT_BUS.register(CreateInteractiveModForge.class);

        var MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();

        CreateInteractiveMod.INSTANCE.getREGISTRATE().registerEventListeners(MOD_BUS);

        CreateInteractiveMod.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> CreateInteractiveMod::initClient);

        MOD_BUS.addListener(CreateInteractiveModForge::onClientSetup);


        ForgeConfigImpl.register(ModLoadingContext.get());
    }

    //Probably bad but oh well
    @SuppressWarnings("removal")
    private static void onClientSetup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(GameContent.INTERACT_ME.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GameContent.INTERACT_ME_NOT.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent joinEvent) {
        if (joinEvent.getEntity() instanceof ServerPlayer player) {
            CreateInteractiveEventsClient.INSTANCE.onPlayerJoin(player);
        }
    }
}
