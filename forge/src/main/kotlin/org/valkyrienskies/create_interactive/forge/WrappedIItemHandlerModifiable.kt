package org.valkyrienskies.create_interactive.forge

import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable

class WrappedIItemHandlerModifiable(private val toWrap: IItemHandler) : IItemHandlerModifiable {
    override fun getSlots(): Int = toWrap.slots

    override fun getStackInSlot(i: Int): ItemStack = toWrap.getStackInSlot(i)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = toWrap.insertItem(slot, stack, simulate)

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = toWrap.extractItem(slot, amount, simulate)

    override fun getSlotLimit(i: Int): Int = toWrap.getSlotLimit(i)

    override fun isItemValid(i: Int, arg: ItemStack): Boolean = toWrap.isItemValid(i, arg)

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        extractItem(slot, getSlotLimit(slot), true)
        insertItem(slot, stack, true)
    }
}