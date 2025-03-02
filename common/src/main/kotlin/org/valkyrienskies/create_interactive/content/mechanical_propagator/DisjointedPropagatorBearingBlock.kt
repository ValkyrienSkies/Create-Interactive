package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlock
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.services.NoOptimize

class DisjointedPropagatorBearingBlock(properties: Properties): MechanicalBearingBlock(properties), ICogWheel {
    @NoOptimize
    override fun getBlockEntityClass(): Class<MechanicalBearingBlockEntity> = DisjointedPropagatorBearingBlockEntity::class.java as Class<MechanicalBearingBlockEntity>

    @NoOptimize
    override fun getBlockEntityType(): BlockEntityType<out DisjointedPropagatorBearingBlockEntity> = GameContent.DISJOINTED_PROPAGATOR_BEARING_BE.get()


    /**
     * Don't clip the sides of blocks
     */
    @NoOptimize
    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return Shapes.empty()
    }

    @NoOptimize
    override fun isSmallCog(): Boolean {
        return true
    }

    @NoOptimize
    override fun hasShaftTowards(world: LevelReader?, pos: BlockPos?, state: BlockState?, face: Direction?): Boolean {
        return false
    }
}