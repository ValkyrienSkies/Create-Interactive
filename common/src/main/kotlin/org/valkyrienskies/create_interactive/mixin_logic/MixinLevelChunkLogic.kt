package org.valkyrienskies.create_interactive.mixin_logic

import com.jozufozu.flywheel.backend.Backend
import com.jozufozu.flywheel.backend.instancing.InstancedRenderDispatcher
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.shipIdToContraptionEntityClient
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.shipIdToContraptionEntityServer
import org.valkyrienskies.create_interactive.mixin_logic.client.MixinInstanceManagerLogic.shouldRemoveBlockEntityInShip
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.yRange
import java.lang.ref.WeakReference

internal object MixinLevelChunkLogic {
    internal fun postSetBlockState(level: Level, pos: BlockPos, state: BlockState?, blockEntities: Map<BlockPos, BlockEntity?>) {
        val ship: Ship = level.getShipManagingPos(pos) ?: return
        val contraptionEntityWeakReference: WeakReference<AbstractContraptionEntity>? = if (level.isClientSide) {
            shipIdToContraptionEntityClient[ship.id]
        } else {
            shipIdToContraptionEntityServer[ship.id]
        }
        if (contraptionEntityWeakReference == null) return
        val contraptionEntity = contraptionEntityWeakReference.get() ?: return

        // Anchor at ship center
        val shipCenter: Vector3ic = ship.chunkClaim.getCenterBlockCoordinates(level.yRange, Vector3i())
        val relativePos = pos.subtract(shipCenter.toBlockPos())

        // Set the block
        val info = StructureTemplate.StructureBlockInfo(relativePos, state, blockEntities[pos]?.saveWithFullMetadata())
        if (contraptionEntity is CarriageContraptionEntity) {
            contraptionEntity.carriage.forEachPresentEntity {
                (it.contraption as ContraptionDuck).`ci$setBlock`(relativePos, info)
            }
        } else {
            (contraptionEntity.contraption as ContraptionDuck).`ci$setBlock`(relativePos, info)
        }
        if (level.isClientSide) {
            if (Backend.isOn()) {
                val blockEntity: BlockEntity? = blockEntities[pos]
                if (blockEntity != null && shouldRemoveBlockEntityInShip(blockEntity)) {
                    // Remove block entity flywheel instances if tile entity is on a shadow ship
                    InstancedRenderDispatcher.getBlockEntities(level).remove(blockEntity)
                }
            }
        }
    }
}
