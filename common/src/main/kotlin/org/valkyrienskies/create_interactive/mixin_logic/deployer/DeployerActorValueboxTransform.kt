package org.valkyrienskies.create_interactive.mixin_logic.deployer

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.kinetics.deployer.DeployerBlock
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour
import dev.engine_room.flywheel.lib.transform.TransformStack
import net.createmod.catnip.math.AngleHelper
import net.createmod.catnip.math.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.create_interactive.services.NoOptimize


class DeployerActorValueboxTransform : ValueBoxTransform.Sided() {
    @NoOptimize
    override fun getLocalOffset(level: LevelAccessor, pos: BlockPos, state: BlockState): Vec3 {
        val facing = state.getValue(DeployerBlock.FACING)
        var vec = VecHelper.voxelSpace(8.0, 8.0, 15.5)
        vec = VecHelper.rotateCentered(vec, AngleHelper.horizontalAngle(side).toDouble(), Direction.Axis.Y)
        vec = VecHelper.rotateCentered(vec, AngleHelper.verticalAngle(side).toDouble(), Direction.Axis.X)
// shift along the facing axis in world space, with the sign you want
        vec = vec.add(Vec3.atLowerCornerOf(facing.normal).scale(4.0 / 16.0))
        return vec
    }

    @NoOptimize
    override fun isSideActive(state: BlockState, direction: Direction): Boolean {
        val facing = state.getValue(DeployerBlock.FACING)
        if (direction.axis === facing.axis) return false
        return (state.block as DeployerBlock).getRotationAxis(state) !== direction.axis
    }

    @NoOptimize
    override fun rotate(level: LevelAccessor, pos: BlockPos, state: BlockState, ms: PoseStack) {
        val facing: Direction = side
        val xRot = (if (facing == Direction.UP) 90 else if (facing == Direction.DOWN) 270 else 0).toFloat()
        val yRot = AngleHelper.horizontalAngle(facing) + 180
        if (facing.axis === Direction.Axis.Y) TransformStack.of(ms)
            .rotateY(Math.toRadians(180.0 + AngleHelper.horizontalAngle(state.getValue(DeployerBlock.FACING))).toFloat())
        TransformStack.of(ms)
            .rotateY(Math.toRadians(yRot.toDouble()).toFloat())
            .rotateX(Math.toRadians(xRot.toDouble()).toFloat())
    }

    @NoOptimize
    override fun getSouthLocation(): Vec3 {
        return Vec3.ZERO
    }

    override fun getScale(): Float {
        return 0.5f
    }

    /**
     * Create stores BE behaviours in a map using their class as the key.
     * We make a custom class for the sole purpose of not overriding VS's scroll options.
     */
    class InteractiveOptionBehaviour<E>
        (
        private val type: BehaviourType<*>, private val nbtKey: String, enum: Class<E>,
        label: Component, be: SmartBlockEntity, slot: ValueBoxTransform
    ) : ScrollOptionBehaviour<E>(enum, label, be, slot) where E : Enum<E>, E : INamedIconOptions {

        override fun getType(): BehaviourType<*> {
            return type
        }

        override fun netId(): Int {
            return 2
        }

        override fun write(nbt: CompoundTag, clientPacket: Boolean) {
            val tmp = CompoundTag()
            super.write(tmp, clientPacket)
            nbt.put(nbtKey, tmp)
        }

        override fun read(nbt: CompoundTag, clientPacket: Boolean) {
            super.read(nbt.getCompound(nbtKey), clientPacket)
        }
    }

    companion object {
        val ACTOR_TYPE: BehaviourType<InteractiveOptionBehaviour<DeployerActorMode>> =
            BehaviourType<InteractiveOptionBehaviour<DeployerActorMode>>()
    }
}