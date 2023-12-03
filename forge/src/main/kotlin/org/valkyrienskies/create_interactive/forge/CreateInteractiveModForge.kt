package org.valkyrienskies.create_interactive.forge

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.valkyrienskies.create_interactive.CreateInteractiveMod
import org.valkyrienskies.create_interactive.CreateInteractiveMod.init
import org.valkyrienskies.create_interactive.CreateInteractiveMod.initClient
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(CreateInteractiveMod.MOD_ID)
class CreateInteractiveModForge {
    init {
        val MOD_BUS = FMLJavaModLoadingContext.get().modEventBus
        // val LOADING_CONTEXT = FMLJavaModLoadingContext.get()
        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }
        MOD_BUS.addListener { event: ModelRegistryEvent? ->
            onModelRegistry(
                event
            )
        }
        MOD_BUS.addListener { event: RegisterRenderers ->
            entityRenderers(
                event
            )
        }

        CreateInteractiveModForgeHelper.registerRegistrate(MOD_BUS)

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT) {
            Runnable {
                initClient()
            }
        }

        /*
        LOADING_CONTEXT.registerExtensionPoint(
            ConfigGuiFactory::class.java
        ) {
            ConfigGuiFactory { _: Minecraft?, parent: Screen? ->
                VS2KotlinHelper.createConfigScreenFor(
                    parent!!,
                    getRegisteredConfig(CreateInteractiveConfig::class.java)
                )
            }
        }
         */
        init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
    }

    private fun entityRenderers(event: RegisterRenderers) {}

    private fun onModelRegistry(event: ModelRegistryEvent?) {}

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
