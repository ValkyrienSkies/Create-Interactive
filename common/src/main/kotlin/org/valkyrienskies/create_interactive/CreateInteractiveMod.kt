package org.valkyrienskies.create_interactive

import com.simibubi.create.foundation.config.ConfigBase
import com.simibubi.create.foundation.data.CreateRegistrate
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.CreativeModeTab
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.config.ModConfig
import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.create_interactive.config.CreateInteractiveConfigs
import java.util.*
import java.util.function.Supplier


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

    private fun registerCommonEvents() {
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
            .icon { GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK.asStack() }
            .displayItems { _, output ->
                output.accept(GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK.asItem())
                output.accept(GameContent.BUFFER_STOP_BLOCK.asItem())
                output.accept(GameContent.INTERACT_ME.asItem())
                output.accept(GameContent.INTERACT_ME_NOT.asItem())
            }
            .build()
    }

}
