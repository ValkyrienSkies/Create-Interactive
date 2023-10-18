package org.valkyrienskies.create_interactive.gui.shiphelm

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import org.valkyrienskies.create_interactive.CreateInteractiveConfig
import org.valkyrienskies.create_interactive.EurekaScreens
import org.valkyrienskies.create_interactive.blockentity.ShipHelmBlockEntity

class ShipHelmScreenMenu(syncId: Int, playerInv: Inventory, val blockEntity: ShipHelmBlockEntity?) :
    AbstractContainerMenu(EurekaScreens.SHIP_HELM.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)

    // TODO this isn't synced...
    val aligning = blockEntity?.aligning ?: false
    val assembled = blockEntity?.assembled ?: false

    override fun stillValid(player: Player): Boolean = true

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (blockEntity == null) return false

        if (id == 0 && !assembled && !player.level.isClientSide) {
            blockEntity.assemble(player)
            return true
        }

        if (id == 1 && assembled && !player.level.isClientSide) {
            blockEntity.align()
            return true
        }

        if (id == 3 && assembled && !player.level.isClientSide && CreateInteractiveConfig.SERVER.allowDisassembly) {
            blockEntity.disassemble()
            return true
        }

        return super.clickMenuButton(player, id)
    }

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> ShipHelmScreenMenu = ::ShipHelmScreenMenu
    }
}
