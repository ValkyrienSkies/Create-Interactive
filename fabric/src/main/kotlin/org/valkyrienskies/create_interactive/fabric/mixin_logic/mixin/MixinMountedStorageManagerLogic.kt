package org.valkyrienskies.create_interactive.fabric.mixin_logic.mixin

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageWrapper
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorageType
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.logistics.vault.ItemVaultBlockEntity
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.entity.ChestBlockEntity
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.mod.common.shipObjectWorld

internal object MixinMountedStorageManagerLogic {
    internal fun preEntityTick(
        entity: AbstractContraptionEntity,
        shipId: ShipId?,
        inventories: MutableMap<BlockPos, MountedItemStorage>,
        fluidInventories: MutableMap<BlockPos, MountedFluidStorage>
    ) {
        // Recreate inventories
        if (shipId != null) {
            val serverShip: ServerShip? =
                (entity.level() as ServerLevel).shipObjectWorld.allShips.getById(shipId)
            serverShip?.activeChunksSet?.forEach { chunkX: Int, chunkZ: Int ->
                val chunk = entity.level().getChunk(chunkX, chunkZ)
                for (be in chunk.blockEntities.values) {
                    val itemType: MountedItemStorageType<*>? = MountedItemStorageType.REGISTRY.get(be.blockState.block)
                    if (itemType != null) {
                        val storage: MountedItemStorage? = itemType.mount(be.level, be.blockState, be.blockPos, be)
                        if (storage != null) {
                            inventories[be.blockPos] = storage
                        }
                    }

                    val fluidType: MountedFluidStorageType<*>? = MountedFluidStorageType.REGISTRY.get(be.blockState.block)
                    if (fluidType != null) {
                        val storage: MountedFluidStorage? = fluidType.mount(be.level, be.blockState, be.blockPos, be)
                        if (storage != null) {
                            fluidInventories[be.blockPos] = storage
                        }
                    }
                }
            }
        } else {
            // Empty storages
            inventories.clear()
            fluidInventories.clear()
        }
    }
}
