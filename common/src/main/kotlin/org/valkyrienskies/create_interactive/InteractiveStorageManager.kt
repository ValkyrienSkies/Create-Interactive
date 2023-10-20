package org.valkyrienskies.create_interactive

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.MountedStorageManager
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity
import com.simibubi.create.foundation.fluid.CombinedTankWrapper
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil
import io.github.fabricators_of_create.porting_lib.util.FluidStack
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld

class InteractiveStorageManager : MountedStorageManager() {
    private var shipId: ShipId? = null

    private val externalStorages: MutableList<Storage<ItemVariant>> = ArrayList()

    override fun entityTick(entity: AbstractContraptionEntity) {
        if (entity.level.isClientSide) return

        // Recreate inventories
        val duck = entity as AbstractContraptionEntityDuck
        shipId = duck.shadowShipId
        if (shipId != null) {
            val serverShip = (entity.level as ServerLevel).shipObjectWorld.allShips.getById(shipId!!)
            val inventories: MutableList<Storage<ItemVariant>> = ArrayList()
            val fuelInventories: MutableList<Storage<ItemVariant>> = ArrayList()
            // TODO: Support fluid inventories
            serverShip!!.activeChunksSet.forEach { chunkX, chunkZ ->
                val chunkAccessor = entity.level.getChunk(chunkX, chunkZ)
                chunkAccessor.blockEntities.forEach { (_, be) ->
                    when (be) {
                        is ChestBlockEntity -> {
                            val newInv = InventoryStorage.of(be, null)
                            inventories.add(newInv)
                            fuelInventories.add(newInv)
                        }

                        is ItemVaultBlockEntity -> {
                            // Not a fuel inventory, just a regular one
                            inventories.add(be.inventoryOfBlock)
                        }

                        else -> {
                            val newInv = TransferUtil.getItemStorage(be) ?: return@forEach
                            inventories.add(newInv)
                            fuelInventories.add(newInv)
                        }
                    }
                }
            }
            inventories.addAll(externalStorages)
            fuelInventories.addAll(externalStorages)
            inventory = wrapItems(inventories, false)
            fuelInventory = wrapItems(fuelInventories, true)
            // TODO: Fix this!!!!!
            fluidInventory = EMPTY_FLUID_WRAPPER
        } else {
            // Empty storages
            inventory = EMPTY_INV_WRAPPER
            fuelInventory = EMPTY_INV_WRAPPER
            fluidInventory = EMPTY_FLUID_WRAPPER
        }
    }

    override fun createHandlers() {
        // Empty storages
        inventory = EMPTY_INV_WRAPPER
        fuelInventory = EMPTY_INV_WRAPPER
        fluidInventory = EMPTY_FLUID_WRAPPER
    }

    override fun addBlock(localPos: BlockPos?, be: BlockEntity?) {
        // Do nothing
    }

    override fun read(
        nbt: CompoundTag?,
        presentBlockEntities: MutableMap<BlockPos, BlockEntity>?,
        clientPacket: Boolean
    ) {
        // Do nothing
    }

    override fun bindTanks(presentBlockEntities: MutableMap<BlockPos, BlockEntity>?) {
        // Do nothing
    }

    override fun write(nbt: CompoundTag?, clientPacket: Boolean) {
        // Do nothing
    }

    override fun removeStorageFromWorld() {
        // Do nothing
    }

    override fun addStorageToWorld(block: StructureTemplate.StructureBlockInfo?, blockEntity: BlockEntity?) {
        // Do nothing
    }

    override fun clear() {
        // Don't clear
    }

    override fun updateContainedFluid(localPos: BlockPos?, containedFluid: FluidStack?) {
        // Do nothing
    }

    override fun attachExternal(externalStorage: Storage<ItemVariant>?) {
        if (externalStorage == null) return
        externalStorages.add(externalStorage)
    }

    override fun handlePlayerStorageInteraction(
        contraption: Contraption?,
        player: Player?,
        localPos: BlockPos?
    ): Boolean {
        // Do nothing
        return false
    }

    companion object {
        private val EMPTY_INV_WRAPPER = Contraption.ContraptionInvWrapper()
        private val EMPTY_FLUID_WRAPPER = CombinedTankWrapper()
    }
}
