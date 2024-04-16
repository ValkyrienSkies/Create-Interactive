package org.valkyrienskies.create_interactive.content.propagator

import com.simibubi.create.foundation.block.IBE
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.content.propagating_axis.PropagatingAxisBlock
import org.valkyrienskies.create_interactive.services.NoOptimize

//object PropagatorBlock : PropagatingAxisBlock(
//    Properties.of().mapColor(MapColor.PODZOL)
//), IBE<PropagatorBlockEntity> {
    //@NoOptimize
    //override fun getBlockEntityClass(): Class<PropagatorBlockEntity> = PropagatorBlockEntity::class.java

   // @NoOptimize
   // override fun getBlockEntityType(): BlockEntityType<out PropagatorBlockEntity> = GameContent.PROPAGATOR_BE.get()

//    @NoOptimize
//    override fun hasShaftTowards(world: LevelReader, pos: BlockPos, state: BlockState, face: Direction): Boolean {
//        return state.getValue(AXIS) != face.axis
//    }
//}
