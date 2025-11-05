package org.valkyrienskies.create_interactive.forge.mixin_logic.mixin

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity
import com.simibubi.create.foundation.fluid.CombinedTankWrapper
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandlerModifiable
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.create_interactive.VS2KotlinHelper
import org.valkyrienskies.create_interactive.forge.WrappedIItemHandlerModifiable
import org.valkyrienskies.create_interactive.forge.mixinducks.CombinedInvWrapperDuck
import org.valkyrienskies.create_interactive.forge.mixinducks.CombinedTankWrapperDuck

internal object MixinMountedStorageManagerLogic {
    internal fun preEntityTick(
        entity: AbstractContraptionEntity,
        shipId: ShipId?,
        externalStorages: List<IItemHandlerModifiable>,
        inventory: MountedItemStorageWrapper,
        fuelInventory: MountedItemStorageWrapper,
        fluidInventory: CombinedTankWrapper,
    ) {
        if (shipId != null) {
            val serverShip: ServerShip? = VS2KotlinHelper.getShipById(entity.level() as ServerLevel, shipId)
            val inventories: MutableList<IItemHandlerModifiable> = ArrayList()
            val fuelInventories: MutableList<IItemHandlerModifiable> = ArrayList()
            val fluidInventories: MutableList<IFluidHandler> = ArrayList()
            serverShip?.activeChunksSet?.forEach { chunkX: Int, chunkZ: Int ->
                val chunk = entity.level().getChunk(chunkX, chunkZ)
                for (be in chunk.blockEntities.values) {
                    // TODO: Do we want to do this?
                    // if (!MountedStorage.canUseAsStorage(be)) {
                    //     continue;
                    // }
                    if (be is ItemVaultBlockEntity) {
                        inventories.add(be.inventoryOfBlock)
                    } else {
                        val newInv =
                            be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve()
                                .orElse(null)
                        if (newInv != null) {
                            if (newInv is IItemHandlerModifiable) {
                                inventories.add(newInv)
                                fuelInventories.add(newInv)
                            } else {
                                // Wrap newInv
                                val wrappedNewInv: IItemHandlerModifiable = WrappedIItemHandlerModifiable(newInv)
                                inventories.add(wrappedNewInv)
                                fuelInventories.add(wrappedNewInv)
                            }
                        }
                    }
                }
                for (be in chunk.blockEntities.values) {
                    val newFluidInv =
                        be.getCapability(ForgeCapabilities.FLUID_HANDLER)
                            .orElse(null)
                            ?: continue
                    // TODO: Do we want to do this?
                    // if (!(teHandler instanceof SmartFluidTank))
                    //     continue;
                    fluidInventories.add(newFluidInv)
                }
            }
            inventories.addAll(externalStorages)
            fuelInventories.addAll(externalStorages)
            (inventory as CombinedInvWrapperDuck).`ci$setInventories`(inventories)
            (fuelInventory as CombinedInvWrapperDuck).`ci$setInventories`(fuelInventories)
            (fluidInventory as CombinedTankWrapperDuck).`ci$setInventories`(fluidInventories)
        } else {
            // Empty storages
            (inventory as CombinedInvWrapperDuck).`ci$setInventories`(emptyList())
            (fuelInventory as CombinedInvWrapperDuck).`ci$setInventories`(emptyList())
            (fluidInventory as CombinedTankWrapperDuck).`ci$setInventories`(emptyList())
        }
    }
}
