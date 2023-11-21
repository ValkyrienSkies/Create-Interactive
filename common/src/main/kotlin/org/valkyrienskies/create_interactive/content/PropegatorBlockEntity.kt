package org.valkyrienskies.create_interactive.content

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class PropegatorBlockEntity(type: BlockEntityType<out PropegatorBlockEntity>, pos: BlockPos, state: BlockState) :
    PropegatingAxisBlockEntity(type, pos, state) {

    override fun isNoisy(): Boolean = true

    override fun attachKinetics() {
        super.attachKinetics()
        //TODO attach to contraption when possible
    }

    override fun detachKinetics() {
        super.detachKinetics()
        //TODO
    }
}