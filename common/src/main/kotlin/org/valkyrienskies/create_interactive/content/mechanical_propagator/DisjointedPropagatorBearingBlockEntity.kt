package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.simibubi.create.AllSoundEvents
import com.simibubi.create.content.contraptions.AssemblyException
import com.simibubi.create.content.contraptions.ControlledContraptionEntity
import com.simibubi.create.content.contraptions.IControlContraption
import com.simibubi.create.content.contraptions.bearing.BearingBlock
import com.simibubi.create.content.contraptions.bearing.BearingContraption
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock
import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions
import com.simibubi.create.foundation.advancement.AllAdvancements
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
import com.simibubi.create.foundation.utility.ServerSpeedProvider
import com.simibubi.create.infrastructure.config.AllConfigs
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Mth
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.mixin.GeneratingKineticBlockEntityAccessor
import org.valkyrienskies.create_interactive.mixin.KineticBlockEntityAccessor
import org.valkyrienskies.create_interactive.mixin.MechanicalBearingBlockEntityAccessor
import org.valkyrienskies.create_interactive.mixin.SmartBlockEntityAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.create_interactive.services.NoOptimize
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toBlockPos
import kotlin.math.abs
import kotlin.math.sign

class DisjointedPropagatorBearingBlockEntity(
    type: BlockEntityType<out DisjointedPropagatorBearingBlockEntity>, pos: BlockPos, state: BlockState
): MechanicalBearingBlockEntity(type, pos, state) {

    private var disjointAngle: Float = 0.0f
    private var disjointSpeed: Float = 0.0f

    private var prevDisjointAngle: Float = 0.0f
    private var prevDisjointSpeed: Float = 0.0f

    private var shouldUpdateDestinationConnections = true
    private var previousOtherConnection: BlockEntity? = null

    private fun getOtherConnection(): BlockPos? {
        if (level!!.isClientSide) return null
        val contraption = movedContraption ?: return null
        val shipId = (contraption as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return null
        val ship: Ship = level.shipObjectWorld.allShips.getById(shipId) ?: return null
        // Anchor at ship center
        return ship.getChunkClaimCenterPos(level!!).toBlockPos()
    }

    fun getDisjointInterpolatedAngle(partialTicks: Float): Float {
        var partialTicks = partialTicks
        if (isVirtual) return Mth.lerp(partialTicks + .5f, prevDisjointAngle, disjointAngle)
        if (movedContraption == null || movedContraption.isStalled || !running) partialTicks = 0f
        var angularSpeed = getDisjointAngularSpeed()
        if (sequencedAngleLimit >= 0) angularSpeed =
            Mth.clamp(angularSpeed.toDouble(), -sequencedAngleLimit, sequencedAngleLimit).toFloat()
        return Mth.lerp(partialTicks, disjointAngle, disjointAngle + angularSpeed)
    }

    private fun getDisjointAngularSpeed(): Float {
        var speed = convertToAngular(if (isWindmill) generatedSpeed else disjointSpeed)
        if (disjointSpeed == 0f) speed = 0f
        if (level!!.isClientSide) {
            speed *= ServerSpeedProvider.get()
            speed += clientAngleDiff / 3.0F
        }
        return speed
    }

    private fun onDisjointSpeedChanged(prevSpeed: Float) {
        // assembleNextTick = true
        sequencedAngleLimit = -1.0
        if (movedContraption != null && sign(prevSpeed) != sign(getSpeed()) && prevSpeed != 0f) {
            if (!movedContraption.isStalled) {
                angle = Math.round(angle).toFloat()
                applyRotation()
            }
            movedContraption.contraption
                .stop(level)
        }

        if (!isWindmill && sequenceContext != null
            && sequenceContext.instruction() == SequencerInstructions.TURN_ANGLE
        ) sequencedAngleLimit = sequenceContext.getEffectiveValue(
            theoreticalSpeed.toDouble()
        )
        setChanged()
    }

    @NoOptimize
    override fun write(compound: CompoundTag, clientPacket: Boolean) {
        super.write(compound, clientPacket)
        compound.putFloat("DisjointAngle", disjointAngle)
        compound.putFloat("DisjointSpeed", disjointSpeed)
    }

    @NoOptimize
    override fun read(compound: CompoundTag, clientPacket: Boolean) {
        disjointAngle = compound.getFloat("DisjointAngle")
        disjointSpeed = compound.getFloat("DisjointSpeed")
        super.read(compound, clientPacket)
    }

    // Custom Propagation
    @NoOptimize
    override fun addPropagationLocations(
        block: IRotate,
        state: BlockState?,
        neighbours: MutableList<BlockPos?>
    ): MutableList<BlockPos?> {
        val locations = super.addPropagationLocations(block, state, neighbours)
        if (getOtherConnection() != null) {
            locations.add(getOtherConnection())
        }
        return locations
    }

    @NoOptimize
    override fun propagateRotationTo(
        target: KineticBlockEntity, stateFrom: BlockState?, stateTo: BlockState?, diff: BlockPos?,
        connectedViaAxes: Boolean, connectedViaCogs: Boolean
    ): Float {
        if (target.blockPos == getOtherConnection()) {
            val toBlock = stateTo!!.block
            if (toBlock !is IRotate) return 0.0f
            val direction = blockState.getValue(DirectionalKineticBlock.FACING).opposite
            if (!toBlock.hasShaftTowards(target.level, target.blockPos, target.blockState, direction)) {
                return 0.0f
            }
            return 1.0f
        }
        return 0.0f
    }

    // Fixing bearing rotation jank

    @NoOptimize
    override fun applyRotation() {
        if (movedContraption == null) return
        movedContraption.setAngle(disjointAngle)
        val blockState = blockState
        if (blockState.hasProperty(BlockStateProperties.FACING)) movedContraption.rotationAxis =
            blockState.getValue(BlockStateProperties.FACING)
                .axis
    }

    @NoOptimize
    override fun onSpeedChanged(prevSpeed: Float) {
        super.onSpeedChanged(prevSpeed)
        setChanged()
    }

    @NoOptimize
    override fun tick() {
        val smartAccess = this as SmartBlockEntityAccessor
        val kineticSmartAccess = this as KineticBlockEntityAccessor
        val mechAccess = this as MechanicalBearingBlockEntityAccessor

        if (!smartAccess.initialized && hasLevel()) {
            initialize()
            smartAccess.initialized = true
        }

        if (smartAccess.lazyTickCounter-- <= 0) {
            smartAccess.lazyTickCounter = smartAccess.lazyTickRate
            lazyTick()
        }

        if (!level!!.isClientSide && needsSpeedUpdate()) attachKinetics()

        effects.tick()

        kineticSmartAccess.setPreventSpeedUpdate(0)

        if (level!!.isClientSide) {
            tickAudio()
        } else {
            val kinetAccess = (this as KineticBlockEntity) as KineticBlockEntityAccessor
            if (kinetAccess.validationCountdown-- <= 0) {
                kinetAccess.validationCountdown = AllConfigs.server().kinetics.kineticValidationFrequency.get()
                kinetAccess.invokeValidateKinetics()
            }

            if (flickerScore > 0) kinetAccess.flickerTally = flickerScore - 1

            if (kineticSmartAccess.networkDirty) {
                if (hasNetwork()) getOrCreateNetwork().updateNetwork()
                kineticSmartAccess.networkDirty = false
            }

            forEachBehaviour { obj: BlockEntityBehaviour -> obj.tick() }
        }

        val generatingAccess = this as GeneratingKineticBlockEntityAccessor
        if (generatingAccess.reActivateSource) {
            updateGeneratedRotation()
            generatingAccess.reActivateSource = false
        }
        //--


        //todo: This doesn't actually work, so we'll need to figure an alternative out later.
//        if (getOtherConnection() != null) {
//            if (previousOtherConnection != level!!.getBlockEntity(getOtherConnection()!!)) {
//                shouldUpdateDestinationConnections = true
//                previousOtherConnection = level!!.getBlockEntity(getOtherConnection()!!)
//            }
//        }
//
//        if (shouldUpdateDestinationConnections && getOtherConnection() != null) {
//            val be = level!!.getBlockEntity(getOtherConnection()!!)
//            val state = level!!.getBlockState(getOtherConnection()!!)
//            val block = state.block
//
//            if (be is KineticBlockEntity && block is IRotate) {
//                be.addPropagationLocations(block, state, listOf(this.blockPos))
//            }
//            shouldUpdateDestinationConnections = false
//        }

        val pos = blockPos.relative(this.blockState.getValue(BlockStateProperties.FACING).opposite, 1)
        prevDisjointSpeed = disjointSpeed
        prevDisjointAngle = disjointAngle

        val blockEntity = level!!.getBlockEntity(pos)
        if (blockEntity != null) {
            if (blockEntity is KineticBlockEntity) {
                val state = blockEntity.blockState
                val block = state.block
                if (block is IRotate && block.hasShaftTowards(level, pos, state, this.blockState.getValue(BlockStateProperties.FACING))) {
                    this.disjointSpeed = blockEntity.speed

                    // region Used to be onDisjointSpeedChanged
                    mechAccess.assembleNextTick = true
                    mechAccess.sequencedAngleLimit = -1.0

                    if (movedContraption != null && sign(prevDisjointSpeed) != sign(getSpeed()) && prevDisjointSpeed != 0f) {
                        if (!movedContraption.isStalled) {
                            mechAccess.angle = Math.round(mechAccess.angle).toFloat()
                            applyRotation()
                        }
                        movedContraption.contraption.stop(level)
                    }

                    if (!isWindmill && kineticSmartAccess.sequenceContext != null && kineticSmartAccess.sequenceContext.instruction() == SequencerInstructions.TURN_ANGLE)
                        mechAccess.sequencedAngleLimit = kineticSmartAccess.sequenceContext.getEffectiveValue(theoreticalSpeed.toDouble())
                    setChanged()

                    // endregion
                    this.onDisjointSpeedChanged(prevDisjointSpeed)
                }
            }
        } else {
            this.disjointSpeed = 0.0f

            // region Used to be onDisjointSpeedChanged
            mechAccess.assembleNextTick = true
            mechAccess.sequencedAngleLimit = -1.0

            if (movedContraption != null && sign(prevDisjointSpeed) != sign(getSpeed()) && prevDisjointSpeed != 0f) {
                if (!movedContraption.isStalled) {
                    mechAccess.angle = Math.round(mechAccess.angle).toFloat()
                    applyRotation()
                }
                movedContraption.contraption.stop(level)
            }

            if (!isWindmill && kineticSmartAccess.sequenceContext != null && kineticSmartAccess.sequenceContext.instruction() == SequencerInstructions.TURN_ANGLE)
                mechAccess.sequencedAngleLimit = kineticSmartAccess.sequenceContext.getEffectiveValue(theoreticalSpeed.toDouble())
            setChanged()

            // endregion
            this.onDisjointSpeedChanged(prevDisjointSpeed)
        }

        if (movedContraption != null) {
            this.disjointAngle += this.disjointSpeed
        } else {
            this.disjointAngle = 0.0f
        }

        if (level!!.isClientSide) mechAccess.clientAngleDiff /= 2f

        if (!level!!.isClientSide && mechAccess.assembleNextTick) {
            mechAccess.assembleNextTick = false
            if (running) {
                val canDisassemble =
                    mechAccess.movementMode.get() == IControlContraption.RotationMode.ROTATE_PLACE || isNearInitialAngle && mechAccess.movementMode.get() == IControlContraption.RotationMode.ROTATE_PLACE_RETURNED
                if (disjointSpeed == 0f && (canDisassemble || movedContraption == null || movedContraption.contraption
                        .blocks
                        .isEmpty())
                ) {
                    if (movedContraption != null) movedContraption.contraption.stop(level)
                    disassemble()
                    return
                }
            } else {
                if (disjointSpeed == 0f && !isWindmill) return
                assemble()
            }
        }

        if (!running) return

        if (!(movedContraption != null && movedContraption.isStalled)) {
            var angularSpeed = getDisjointAngularSpeed()
            if (mechAccess.sequencedAngleLimit >= 0) {
                angularSpeed = Mth.clamp(angularSpeed.toDouble(), -mechAccess.sequencedAngleLimit, mechAccess.sequencedAngleLimit).toFloat()
                mechAccess.sequencedAngleLimit = 0.0.coerceAtLeast(mechAccess.sequencedAngleLimit - abs(angularSpeed))
            }
            val newAngle = disjointAngle + angularSpeed
            disjointAngle = (newAngle % 360)
        }

        this.applyRotation()
    }

    @NoOptimize
    override fun assemble() {
        if (level!!.getBlockState(worldPosition)
                .block !is BearingBlock
        ) return

        val direction = blockState.getValue(BearingBlock.FACING)
        val contraption = BearingContraption(
            isWindmill, direction
        )
        try {
            if (!contraption.assemble(level, worldPosition)) return
            lastException = null
        } catch (e: AssemblyException) {
            lastException = e
            sendData()
            return
        }

        if (isWindmill) award(AllAdvancements.WINDMILL)
        if (contraption.sailBlocks >= 16 * 8) award(AllAdvancements.WINDMILL_MAXED)

        contraption.removeBlocksFromWorld(level, BlockPos.ZERO)
        movedContraption = ControlledContraptionEntity.create(level, this, contraption)
        val anchor = worldPosition.relative(direction)
        movedContraption.setPos(anchor.x.toDouble(), anchor.y.toDouble(), anchor.z.toDouble())
        movedContraption.rotationAxis = direction.axis
        level!!.addFreshEntity(movedContraption)

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition)

        if (contraption.containsBlockBreakers()) award(AllAdvancements.CONTRAPTION_ACTORS)

        running = true
        disjointAngle = 0f
        sendData()
        updateGeneratedRotation()
    }

    @NoOptimize
    override fun disassemble() {
        if (!running && movedContraption == null) return
        disjointAngle = 0f
        sequencedAngleLimit = -1.0
        if (isWindmill) applyRotation()
        if (movedContraption != null) {
            movedContraption.disassemble()
            AllSoundEvents.CONTRAPTION_DISASSEMBLE.playOnServer(level, worldPosition)
        }

        movedContraption = null
        running = false
        updateGeneratedRotation()
        assembleNextTick = false
        sendData()
    }
}
