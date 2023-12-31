package org.valkyrienskies.create_interactive.forge

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.DeferredRegister
import org.valkyrienskies.create_interactive.CreateInteractiveMod
import org.valkyrienskies.create_interactive.CreateInteractiveMod.init
import org.valkyrienskies.create_interactive.CreateInteractiveMod.initClient
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.function.Supplier

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

        val deferredRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateInteractiveMod.MOD_ID)
        deferredRegister.register("general", Supplier { return@Supplier CreateInteractiveModForgeHelper.createCreativeTab() })
        deferredRegister.register(MOD_BUS)
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
    }

    private fun entityRenderers(event: RegisterRenderers) {}

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
