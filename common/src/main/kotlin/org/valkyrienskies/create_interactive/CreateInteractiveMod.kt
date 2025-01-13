package org.valkyrienskies.create_interactive

import com.simibubi.create.foundation.data.CreateRegistrate
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import org.valkyrienskies.core.impl.hooks.VSEvents


object CreateInteractiveMod {
    const val MOD_ID = "create_interactive"
    val REGISTRATE: CreateRegistrate = CreateRegistrate.create(MOD_ID)
    val BASE_CREATIVE_TAB: CreativeModeTab = CreateInteractiveCreativeTabs.create(
        ResourceLocation(
            MOD_ID,
            "create_interactive"
        )
    ) { GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK.asStack() }

    @JvmStatic
    fun init() {
        REGISTRATE.creativeModeTab { BASE_CREATIVE_TAB }
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
}
