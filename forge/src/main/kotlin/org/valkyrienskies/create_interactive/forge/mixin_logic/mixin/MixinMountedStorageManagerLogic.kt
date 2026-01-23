package org.valkyrienskies.create_interactive.forge.mixin_logic.mixin

import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorage
import com.simibubi.create.api.contraption.storage.fluid.MountedFluidStorageType
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.create_interactive.VS2KotlinHelper
import org.valkyrienskies.create_interactive.forge.InteractiveMountedItemStorage

internal object MixinMountedStorageManagerLogic {
    internal fun preEntityTick(
        entity: AbstractContraptionEntity,
        shipId: ShipId?,
        inventories: MutableMap<BlockPos, InteractiveMountedItemStorage>,
        fluidInventories: MutableMap<BlockPos, MountedFluidStorage>
    ) {
        // Recreate inventories
        if (shipId != null) {
            val serverShip: ServerShip? = VS2KotlinHelper.getShipById(entity.level() as ServerLevel, shipId)
            serverShip?.activeChunksSet?.forEach { chunkX: Int, chunkZ: Int ->
                val chunk = entity.level().getChunk(chunkX, chunkZ)
                for (be in chunk.blockEntities.values) {
                    val itemType: MountedItemStorageType<*>? = MountedItemStorageType.REGISTRY.get(be.blockState.block)
                    if (itemType != null) {
                        val storage: MountedItemStorage? = itemType.mount(be.level, be.blockState, be.blockPos, be)
                        if (storage != null) {
                            inventories[be.blockPos] = InteractiveMountedItemStorage(be, storage)
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
