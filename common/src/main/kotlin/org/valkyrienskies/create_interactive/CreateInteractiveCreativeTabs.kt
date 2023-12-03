package org.valkyrienskies.create_interactive

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.create_interactive.services.CreateInteractivePlatformHelper
import java.util.ServiceLoader

object CreateInteractiveCreativeTabs {
    fun create(id: ResourceLocation, stack: () -> ItemStack): CreativeModeTab {
        return ServiceLoader.load(CreateInteractivePlatformHelper::class.java)
            .findFirst()
            .get()
            .createCreativeTab(id, stack)
    }
}
