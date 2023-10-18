package org.valkyrienskies.create_interactive

import net.minecraft.core.Registry
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import org.valkyrienskies.create_interactive.gui.engine.EngineScreenMenu
import org.valkyrienskies.create_interactive.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.create_interactive.registry.DeferredRegister

private typealias HFactory<T> = (syncId: Int, playerInv: Inventory) -> T

@Suppress("unused")
object EurekaScreens {
    private val SCREENS = DeferredRegister.create(CreateInteractiveMod.MOD_ID, Registry.MENU_REGISTRY)

    val SHIP_HELM = ShipHelmScreenMenu.factory withName "ship_helm"
    val ENGINE = EngineScreenMenu.factory withName "engine"

    fun register() {
        SCREENS.applyAll()
    }

    private infix fun <T : AbstractContainerMenu> HFactory<T>.withName(name: String) =
        SCREENS.register(name) { MenuType(this) }
}
