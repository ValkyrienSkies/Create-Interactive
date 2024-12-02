package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllInteractionBehaviours
import com.simibubi.create.AllMovementBehaviours
import com.simibubi.create.content.contraptions.AbstractContraptionEntity
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.StructureTransform
import com.simibubi.create.content.contraptions.actors.contraptionControls.ContraptionControlsMovement
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock
import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.CarriageContraption
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity
import com.simibubi.create.foundation.utility.Couple
import com.simibubi.create.foundation.utility.Iterate
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import net.minecraft.world.phys.AABB
import org.apache.commons.lang3.tuple.MutablePair
import org.joml.Vector3ic
import org.valkyrienskies.create_interactive.CreateActor
import org.valkyrienskies.create_interactive.CreateActorImmutable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.attemptTrainRelocation
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.createShipForContraption
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.mixin.CarriageBogeyAccessor
import org.valkyrienskies.create_interactive.mixin.CarriageContraptionAccessor
import org.valkyrienskies.create_interactive.mixin.ContraptionAccessor
import org.valkyrienskies.create_interactive.mixin.StructureBlockInfoAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML

internal object MixinContraptionLogic {
    internal fun preOnEntityCreated(
        initialBlocks: Map<BlockPos, StructureTemplate.StructureBlockInfo>,
        entity: AbstractContraptionEntity
    ) {
        val level = entity.level()
        if (level.isClientSide) {
            return
        }
        val prevId = (entity as AbstractContraptionEntityDuck).`ci$getShadowShipId`()
        if (prevId != null && (level as ServerLevel).shipObjectWorld.allShips.getById(prevId) != null) {
            // If shadow ship already exists then don't make a new one
            return
        }

        if (!CreateInteractiveUtil.checkContraptionEnabled(entity.contraption)) {
            return
        }

        val nonBrittleBlocks = entity.contraption.blocks
        if (!CreateInteractiveUtil.checkInteractMeSticker(nonBrittleBlocks.entries)) {
            return
        }

        if (CreateInteractiveUtil.checkInteractMeNotSticker(nonBrittleBlocks.entries)) {
            return
        }

        if (entity.contraption.javaClass.packageName.contains("createbigcannons")) {
            // Do not create shadow ships for CBC, too hard
            return
        }

        val blockPos = BlockPos.containing(entity.position())
        val shipId = createShipForContraption(level as ServerLevel, entity.contraption, blockPos, initialBlocks) ?: return
        (entity as AbstractContraptionEntityDuck).`ci$setShadowShipId`(shipId)
    }

    internal fun preAddBlocksToWorld(
        disassembled: Boolean,
        entity: AbstractContraptionEntity?,
        blocks: MutableMap<BlockPos, StructureTemplate.StructureBlockInfo>,
        world: Level,
        getBlockEntityNBT: (Level, BlockPos) -> CompoundTag?
    ) {
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
        val shadowShipId = duck.`ci$getShadowShipId`() ?: return
        val ship = world.shipObjectWorld.allShips.getById(shadowShipId) ?: return
        // Anchor at ship center
        val shipCenter: Vector3ic = ship.getChunkClaimCenterPos(world)

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

    /**
     * Relocate trains on the ship back to the world
     */
    internal fun postAddBlocksToWorld(
        entity: AbstractContraptionEntity,
        blocks: MutableMap<BlockPos, StructureTemplate.StructureBlockInfo>,
        world: Level,
        transform: StructureTransform,
    ) {
        if (world !is ServerLevel) return
        val shipId = (entity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return
        val shipFor = world.shipObjectWorld.allShips.getById(shipId) ?: return
        val shipCenter = shipFor.getChunkClaimCenterPos(world)
        attemptTrainRelocation(
            world, shipCenter.toBlockPos(), blocks, entity.contraption.anchor.toJOML(), transform
        )
    }

    private fun <K, V> MutableMap<K, V>.removeValues(value: V): List<K> {
        val removed: MutableList<K> = ArrayList(0)
        val it = iterator()
        while (it.hasNext()) {
            val next = it.next()
            if (next.value == value) {
                removed.add(next.key)
                it.remove()
            }
        }
        return removed
    }

    // Ideally this would be null, but just use this instead to fix NPE
    private val INVALID_SEAT_POS = BlockPos(-100000, 0, -100000)

    internal fun setBlock(
        blocks: MutableMap<BlockPos, StructureTemplate.StructureBlockInfo>,
        actors: MutableList<CreateActor>,
        bounds: AABB,
        localPos: BlockPos,
        structureBlockInfo: StructureTemplate.StructureBlockInfo,
        setBounds: (AABB) -> Unit,
        disableActorOnStart: (MovementContext) -> Unit,
        changedActors: MutableSet<BlockPos>,
        interactors: MutableMap<BlockPos, MovingInteractionBehaviour>,
        contraption: Contraption,
    ) {
        val prevBlockState = blocks[localPos]?.state
        if (structureBlockInfo.state.block != Blocks.AIR) {
            blocks[localPos] = structureBlockInfo
            setBounds(bounds.minmax(AABB(localPos)))
        } else {
            // Remove air blocks
            blocks.remove(localPos)
        }

        // Update seats
        val prevWasSeat = prevBlockState?.block is SeatBlock
        val newIsSeat = structureBlockInfo.state.block is SeatBlock
        if (prevWasSeat && !newIsSeat) {
            // Remove existing seat
            val indexOf = contraption.seats.indexOf(localPos)
            if (indexOf != -1) {
                contraption.seats[indexOf] = INVALID_SEAT_POS
                // Remove values from seatMapping that are equal to [indexOf]
                val removedEntities = contraption.seatMapping.removeValues(indexOf)
                removedEntities.forEach { uuid ->
                    contraption.entity.passengers.find { it.uuid == uuid }?.removeVehicle()
                }
                // Remove the conductor seat
                if (contraption is CarriageContraption) {
                    contraption.conductorSeats.remove(localPos)
                }
            }
        } else if (!prevWasSeat && newIsSeat) {
            // Add a new seat, fill an empty seat index if one exists, otherwise append to the seats list
            val invalidIndexOf = contraption.seats.indexOf(INVALID_SEAT_POS)
            if (invalidIndexOf != -1) {
                contraption.seats[invalidIndexOf] = localPos
            } else {
                contraption.seats.add(localPos)
            }
            // Add a conductor seat
            if (contraption is CarriageContraption) {
                for (direction in Iterate.directionsInAxis(contraption.assemblyDirection.axis)) {
                    if (contraption.inControl(localPos, direction)) {
                        contraption.conductorSeats.computeIfAbsent(localPos) { Couple.create(false, false) }
                            .set(direction != contraption.assemblyDirection, true)
                    }
                }
            }
        }

        if (contraption is CarriageContraption) {
            // Add/Remove conductor seats when train controls are placed
            val prevWasTrainControls = if (prevBlockState != null) AllBlocks.TRAIN_CONTROLS.has(prevBlockState) else false
            val newIsTrainControls = AllBlocks.TRAIN_CONTROLS.has(structureBlockInfo.state)
            if (prevWasTrainControls || newIsTrainControls) {
                // Remove the train controls, remove any conductor seats using this
                for (direction in Iterate.directionsInAxis(contraption.assemblyDirection.axis)) {
                    val seatPos = localPos.relative(direction.opposite)
                    // Remove the conductor seat
                    contraption.conductorSeats.remove(seatPos)
                    // Add it again if the conductor seat is still valid
                    for (direction2 in Iterate.directionsInAxis(contraption.assemblyDirection.axis)) {
                        if (contraption.inControl(seatPos, direction2)) {
                            contraption.conductorSeats.computeIfAbsent(seatPos) { Couple.create(false, false) }
                                .set(direction2 != contraption.assemblyDirection, true)
                        }
                    }
                }
            }

            val prevWasBurner = prevBlockState?.block is BlazeBurnerBlock
            val newIsBurner = structureBlockInfo.state.block is BlazeBurnerBlock
            if (prevWasBurner && !newIsBurner) {
                (contraption as CarriageContraptionAccessor).assembledBlazeBurners.remove(localPos)
            } else if (!prevWasBurner && newIsBurner) {
                (contraption as CarriageContraptionAccessor).assembledBlazeBurners.add(localPos)
                for (direction in Iterate.directionsInAxis(contraption.assemblyDirection.axis)) {
                    if (contraption.inControl(localPos, direction)) {
                        contraption.blazeBurnerConductors.set(direction != contraption.assemblyDirection, true)
                    }
                }
            }
        }

        val newBehavior = AllMovementBehaviours.getBehaviour(structureBlockInfo.state)
        // Don't create actors for new bearings
        if (newBehavior != null && newBehavior !is StabilizedBearingMovementBehaviour) {
            val context = MovementContext(
                contraption.entity.level(), structureBlockInfo, contraption
            )
            val behaviour = AllMovementBehaviours.getBehaviour(structureBlockInfo.state)
            behaviour?.startMoving(context)
            if (behaviour is ContraptionControlsMovement) disableActorOnStart(context)
            actors.removeIf { next: CreateActor -> next.left.pos == structureBlockInfo.pos }
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
                actors.removeIf { next: CreateActor -> next.left.pos == structureBlockInfo.pos }
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

    internal fun hasActorAtPos(
        localPos: BlockPos, actors: List<CreateActor>
    ): Boolean = getActorAtPos(localPos, actors) != null

    internal fun hasBogeyAtPos(entity: AbstractContraptionEntity, localPos: BlockPos): Boolean {
        if (entity !is CarriageContraptionEntity) return false
        val carriage: Carriage = entity.carriage
        val bogeySpacing = carriage.bogeySpacing
        for (bogey in carriage.bogeys) {
            if (bogey == null) continue
            val bogeyPos =
                if ((bogey as CarriageBogeyAccessor).getIsLeading()) BlockPos.ZERO else BlockPos.ZERO.relative(
                    entity.initialOrientation.counterClockWise, bogeySpacing
                )
            if (bogeyPos == localPos) {
                return true
            }
        }
        return false
    }

    internal fun getActorAtPos(
        localPos: BlockPos, actors: List<CreateActor>
    ): CreateActorImmutable? = actors.firstOrNull { it.left.pos == localPos }

    internal fun preWriteBlocksCompound(contraption: Contraption) {
        val contraptionEntity = contraption.entity ?: return
        val level = contraption.entity.level()
        val shipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return
        val ship = level.shipObjectWorld.allShips.getById(shipId) ?: return
        val centerPos = ship.getChunkClaimCenterPos(level)
        for (block in contraption.blocks.values) {
            // Update the nbt of each tile entity
            val pos = block.pos.offset(centerPos.x(), centerPos.y(), centerPos.z())
            (block as StructureBlockInfoAccessor).setNbt((contraption as ContraptionAccessor).invokeGetBlockEntityNBT(level, pos))
        }
    }
}
