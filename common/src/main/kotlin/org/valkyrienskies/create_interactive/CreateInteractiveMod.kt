package org.valkyrienskies.create_interactive

import com.simibubi.create.foundation.data.CreateRegistrate
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.core.impl.hooks.VSEvents

object CreateInteractiveMod {
    const val MOD_ID = "create_interactive"
    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MOD_ID)
    val INTERACTIVE_CREATIVE_TAB = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB, ResourceLocation(
            MOD_ID,
            "create_interactive"
        )
    )

    @JvmStatic
    fun init() {
        VSConfigClass.registerConfig("create_interactive", CreateInteractiveConfig::class.java)
        registerCommonEvents()
        GameContent.init()
    }

    @JvmStatic
    fun initClient() {
        registerClientEvents()
        CreateInteractivePartialModels.init()
    }

    private fun registerCommonEvents() {
        VSEvents.shipUnloadEventClient.on { (clientShip) -> CreateInteractiveUtil.onShipUnloadEventClient(clientShip) }
    }

    private fun registerClientEvents() {
        VSEvents.startUpdateRenderTransformsEvent.on { _ -> CreateInteractiveEventsClient.onStartUpdateRenderTransforms() }
    }

    fun asResource(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }

    fun createCreativeTab(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.createinteractive"))
            .icon { GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK.asStack() }
            .displayItems { _, output ->
                output.accept(GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK.asItem())
                output.accept(GameContent.BUFFER_STOP_BLOCK.asItem())
            }
            .build()
    }
}
