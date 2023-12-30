package org.valkyrienskies.create_interactive.forge

import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import org.valkyrienskies.create_interactive.services.NoOptimize

class WrappedIItemHandlerModifiable(private val toWrap: IItemHandler) : IItemHandlerModifiable {
    @NoOptimize
    override fun getSlots(): Int = toWrap.slots

    @NoOptimize
    override fun getStackInSlot(i: Int): ItemStack = toWrap.getStackInSlot(i)

    @NoOptimize
    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = toWrap.insertItem(slot, stack, simulate)

    @NoOptimize
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = toWrap.extractItem(slot, amount, simulate)

    @NoOptimize
    override fun getSlotLimit(i: Int): Int = toWrap.getSlotLimit(i)

    @NoOptimize
    override fun isItemValid(i: Int, arg: ItemStack): Boolean = toWrap.isItemValid(i, arg)

    @NoOptimize
    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        extractItem(slot, getSlotLimit(slot), true)
        insertItem(slot, stack, true)
    }
}
