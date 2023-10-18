package org.valkyrienskies.create_interactive.services

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

interface CreateInteractivePlatformHelper {
    fun createCreativeTab(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab
}
