package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.AllInteractionBehaviours
import com.simibubi.create.AllMovementBehaviours
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour
import com.simibubi.create.foundation.blockEntity.IMultiBlockEntityContainer
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import net.minecraft.world.phys.AABB
import org.apache.commons.lang3.tuple.MutablePair
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.yRange

internal object MixinContraptionLogic {
    internal fun preOnEntityCreated(initialBlocks: Map<BlockPos, StructureTemplate.StructureBlockInfo>, anchor: BlockPos, entity: AbstractContraptionEntity) {
        val level = entity.level
        if (level.isClientSide) {
            return
        }
        val prevId = (entity as AbstractContraptionEntityDuck).getShadowShipId()
        if (prevId != null && (level as ServerLevel).shipObjectWorld.allShips.getById(prevId) != null) {
            // If shadow ship already exists then don't make a new one
            return
        }

        if (entity.contraption.javaClass.packageName.contains("createbigcannons")) {
            // Do not create shadow ships for CBC, too hard
            return
        }

        val blockPos = BlockPos(entity.position())
        val serverShip: ServerShip =
            (level as ServerLevel).shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, level.dimensionId)
        val shipId: Long = serverShip.id

        // Anchor at ship center
        val shipCenter: Vector3ic = serverShip.chunkClaim.getCenterBlockCoordinates(level.yRange, Vector3i())
        for ((pos, structureInfo) in initialBlocks.entries) {
            val localPos = pos // .subtract(anchor)
            val newPos = localPos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z())
            val flags =
                Block.UPDATE_MOVE_BY_PISTON or Block.UPDATE_SUPPRESS_DROPS or Block.UPDATE_KNOWN_SHAPE or Block.UPDATE_CLIENTS or Block.UPDATE_IMMEDIATE
            level.setBlock(newPos, structureInfo.state, flags)

            // region Copy the tile entity to the ship
            val newBlockEntity = level.getBlockEntity(newPos)
            if (newBlockEntity != null) {
                // Transform the block entity, put it in the ship
                val tag: CompoundTag? = structureInfo.nbt
                if (tag != null) {
                    tag.putInt("x", newPos.x)
                    tag.putInt("y", newPos.y)
                    tag.putInt("z", newPos.z)
                    if (newBlockEntity is IMultiBlockEntityContainer && tag.contains("LastKnownPos")) tag.put(
                        "LastKnownPos", NbtUtils.writeBlockPos(
                            BlockPos.ZERO.below(
                                Int.MAX_VALUE - 1
                            )
                        )
                    )
                    newBlockEntity.load(tag)
                    level.setBlockEntity(newBlockEntity)
                }
            }
            // endregion
        }
        (entity as AbstractContraptionEntityDuck).setShadowShipId(shipId)
    }

    internal fun preAddBlocksToWorld(disassembled: Boolean, entity: AbstractContraptionEntity?, blocks: MutableMap<BlockPos, StructureTemplate.StructureBlockInfo>, world: Level, getBlockEntityNBT: (Level, BlockPos) -> CompoundTag?) {
        if (disassembled) {
            // Do nothing
            return
        }
        val entityCopy: AbstractContraptionEntity? = entity
        if (entityCopy == null) {
            println("Susmogus!")
            return
        }
        val duck = entityCopy as AbstractContraptionEntityDuck
        val shadowShipId = duck.getShadowShipId() ?: return
        val ship = world.shipObjectWorld.allShips.getById(shadowShipId) ?: return
        // Anchor at ship center
        val shipCenter: Vector3ic = ship.chunkClaim.getCenterBlockCoordinates(world.yRange, Vector3i())

        // Update contraption tile entities to match the contents of the shadow ship
        ship.activeChunksSet.forEach { chunkX: Int, chunkZ: Int ->
            val chunkAccess: ChunkAccess = world.getChunk(chunkX, chunkZ)
            for (blockPos in chunkAccess.blockEntitiesPos) {
                val localPos =
                    blockPos.offset(-shipCenter.x(), -shipCenter.y(), -shipCenter.z())
                val blockState = world.getBlockState(blockPos)
                val compoundTag: CompoundTag? = getBlockEntityNBT(world, blockPos)
                val blockInfo =
                    StructureTemplate.StructureBlockInfo(
                        localPos,
                        blockState,
                        compoundTag
                    )
                blocks[localPos] = blockInfo
            }
        }
    }

    internal fun setBlock(
        blocks: MutableMap<BlockPos, StructureTemplate.StructureBlockInfo>,
        actors: MutableList<MutablePair<StructureTemplate.StructureBlockInfo, MovementContext?>>,
        bounds: AABB,
        level: Level?,
        localPos: BlockPos,
        structureBlockInfo: StructureTemplate.StructureBlockInfo,
        setBounds: (AABB) -> Unit,
        disableActorOnStart: (MovementContext) -> Unit,
        changedActors: MutableSet<BlockPos>,
        interactors: MutableMap<BlockPos, MovingInteractionBehaviour>,
        contraption: Contraption,
    ) {
        val prevState: StructureTemplate.StructureBlockInfo? = blocks[localPos]
        if (prevState != null && prevState.state === structureBlockInfo.state) {
            return
        }
        blocks[localPos] = structureBlockInfo
        setBounds(bounds.minmax(AABB(localPos)))
        if (AllMovementBehaviours.getBehaviour(structureBlockInfo.state) != null) {
            val context = MovementContext(
                level, structureBlockInfo, contraption
            )
            val behaviour = AllMovementBehaviours.getBehaviour(structureBlockInfo.state)
            behaviour?.startMoving(context)
            if (behaviour is ContraptionControlsMovement) disableActorOnStart(context)
            actors.removeIf { next: MutablePair<StructureTemplate.StructureBlockInfo, MovementContext?> -> next.left.pos == structureBlockInfo.pos }
            actors.add(
                MutablePair.of(
                    structureBlockInfo,
                    context
                )
            )
            changedActors.add(structureBlockInfo.pos)
        } else {
            // Remove actor if one exists
            val anyRemoved: Boolean =
                actors.removeIf { next: MutablePair<StructureTemplate.StructureBlockInfo, MovementContext?> -> next.left.pos == structureBlockInfo.pos }
            if (anyRemoved) {
                changedActors.add(structureBlockInfo.pos)
            }
        }
        val interactionBehaviour = AllInteractionBehaviours.getBehaviour(structureBlockInfo.state)
        if (interactionBehaviour != null) {
            interactors[localPos] = interactionBehaviour
        } else {
            // Remove interactor if one exists
            interactors.remove(structureBlockInfo.pos)
        }
    }

    internal fun hasActorAtPos(localPos: BlockPos, isCheckingMechanicalBearing: Boolean, actors: List<MutablePair<StructureTemplate.StructureBlockInfo, MovementContext?>>): Boolean {
        for (actor in actors) {
            if (actor.left.pos == localPos) {
                return if (isCheckingMechanicalBearing) {
                    actor.left.nbt != null
                } else true
            }
        }
        return false
    }
}
