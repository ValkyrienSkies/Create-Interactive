package org.valkyrienskies.create_interactive.fabric.mixin_logic

import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity
import com.simibubi.create.foundation.fluid.CombinedTankWrapper
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.ChestBlockEntity
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.mod.common.shipObjectWorld

internal object MixinMountedStorageManagerLogic {
    internal fun preEntityTick(
        entity: AbstractContraptionEntity,
        shipId: ShipId?,
        externalStorages: List<Storage<ItemVariant>>,
        inventory: Contraption.ContraptionInvWrapper,
        fuelInventory: Contraption.ContraptionInvWrapper,
        fluidInventory: CombinedTankWrapper,
    ) {
        // Recreate inventories
        if (shipId != null) {
            val serverShip: ServerShip? =
                (entity.level as ServerLevel).shipObjectWorld.allShips.getById(shipId)
            val inventories: MutableList<Storage<ItemVariant>> = ArrayList()
            val fuelInventories: MutableList<Storage<ItemVariant>> = ArrayList()
            val fluidInventories: MutableList<Storage<FluidVariant>> = ArrayList()
            serverShip?.activeChunksSet?.forEach { chunkX: Int, chunkZ: Int ->
                val chunk = entity.level.getChunk(chunkX, chunkZ)
                for (be in chunk.blockEntities.values) {
                    // TODO: Do we want to do this?
                    // if (!MountedStorage.canUseAsStorage(be)) {
                    //     continue;
                    // }
                    if (be is ChestBlockEntity) {
                        val newInv =
                            InventoryStorage.of(be, null)
                        inventories.add(newInv)
                        fuelInventories.add(newInv)
                    } else if (be is ItemVaultBlockEntity) {
                        inventories.add(be.inventoryOfBlock)
                    } else {
                        val newInv =
                            TransferUtil.getItemStorage(be)
                        if (newInv != null) {
                            inventories.add(newInv)
                            fuelInventories.add(newInv)
                        }
                    }
                }
                for (be in chunk.blockEntities.values) {
                    val newFluidInv =
                        TransferUtil.getFluidStorage(be) ?: continue
                    // TODO: Do we want to do this?
                    // if (!(teHandler instanceof SmartFluidTank))
                    //     continue;
                    fluidInventories.add(newFluidInv)
                }
            }
            inventories.addAll(externalStorages)
            fuelInventories.addAll(externalStorages)
            inventory.parts = inventories
            fuelInventory.parts = fuelInventories
            fluidInventory.parts = fluidInventories
        } else {
            // Empty storages
            inventory.parts = emptyList()
            fuelInventory.parts = emptyList()
            fluidInventory.parts = emptyList()
        }
    }
}
