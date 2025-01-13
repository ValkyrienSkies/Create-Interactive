package org.valkyrienskies.create_interactive.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import org.valkyrienskies.create_interactive.CreateInteractiveEventsClient;
import org.valkyrienskies.create_interactive.CreateInteractiveMod;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class CreateInteractiveModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();

        CreateInteractiveMod.init();
        CreateInteractiveMod.INSTANCE.getREGISTRATE().register();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            CreateInteractiveEventsClient.INSTANCE.onPlayerJoin(player);
        });

        FabricConfigImpl.register();
    }
}
