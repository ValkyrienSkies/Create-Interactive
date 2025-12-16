package org.valkyrienskies.create_interactive

import com.simibubi.create.foundation.data.CreateRegistrate
import dev.engine_room.flywheel.api.visualization.VisualizerRegistry
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.create_interactive.GameContent.MECHANICAL_PROPAGATOR_BEARING_BE
import org.valkyrienskies.create_interactive.content.mechanical_propagator.MechPropBearingInstance
import org.valkyrienskies.create_interactive.content.ponders.InteractivePonderRegistry


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
        registerCommonEvents()
        GameContent.init()
    }

    @JvmStatic
    fun initClient() {
        registerClientEvents()
        CreateInteractivePartialModels.init()
    }

    @JvmStatic
    fun registerVisuals() {
        VisualizerRegistry.setVisualizer(MECHANICAL_PROPAGATOR_BEARING_BE.get(),
            SimpleBlockEntityVisualizer(
                {ctx, be, pt -> MechPropBearingInstance(ctx, be, pt)},
                { _ -> false}
            )
        )
    }

    private fun registerCommonEvents() {
        VSEvents.shipLoadEventClient.on { (clientShip) -> CreateInteractiveUtil.onShipLoadEventClient(clientShip) }
        VSEvents.shipUnloadEventClient.on { (clientShip) -> CreateInteractiveUtil.onShipUnloadEventClient(clientShip) }
    }

    private fun registerClientEvents() {
        VSEvents.startUpdateRenderTransformsEvent.on { _ -> CreateInteractiveEventsClient.onStartUpdateRenderTransforms() }
    }

    fun asResource(path: String): ResourceLocation {
        return ResourceLocation(MOD_ID, path)
    }

    @JvmStatic
    fun createCreativeTab(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.create_interactive"))
            .icon { GameContent.INTERACT_ME.asStack() }
            .displayItems { _, output ->
                output.accept(GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK.asItem())
                output.accept(GameContent.DISJOINTED_PROPAGATOR_BEARING_BLOCK.asItem())
                output.accept(GameContent.BUFFER_STOP_BLOCK.asItem())
                output.accept(GameContent.INTERACT_ME.asItem())
                output.accept(GameContent.INTERACT_ME_NOT.asItem())
            }
            .build()
    }

}
