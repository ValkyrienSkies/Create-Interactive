package org.valkyrienskies.create_interactive.content.mechanical_propagator

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlock
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.services.NoOptimize

class MechanicalPropagatorBearingBlock(properties: Properties): MechanicalBearingBlock(properties) {
    @NoOptimize
    override fun getBlockEntityClass(): Class<MechanicalBearingBlockEntity> = MechanicalPropagatorBearingBlockEntity::class.java as Class<MechanicalBearingBlockEntity>

    @NoOptimize
    override fun getBlockEntityType(): BlockEntityType<out MechanicalPropagatorBearingBlockEntity> = GameContent.MECHANICAL_PROPAGATOR_BEARING_BE.get()

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
}
