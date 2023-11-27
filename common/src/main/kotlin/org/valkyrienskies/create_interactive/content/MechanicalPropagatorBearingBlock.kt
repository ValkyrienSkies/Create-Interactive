package org.valkyrienskies.create_interactive.content

import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlock
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.material.Material
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.services.NoOptimize

object MechanicalPropagatorBearingBlock: MechanicalBearingBlock(Properties.of(Material.STONE)) {
    @NoOptimize
    override fun getBlockEntityClass(): Class<MechanicalBearingBlockEntity> = MechanicalPropagatorBearingBlockEntity::class.java as Class<MechanicalBearingBlockEntity>

    @NoOptimize
    override fun getBlockEntityType(): BlockEntityType<out MechanicalPropagatorBearingBlockEntity> = GameContent.MECHANICAL_PROPAGATOR_BEARING_BE.get()
}
