package org.valkyrienskies.create_interactive.content.buffer_stop

import com.simibubi.create.foundation.block.IBE
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import org.valkyrienskies.create_interactive.GameContent

class BufferStopBlock(properties: Properties): HorizontalDirectionalBlock(properties), IBE<BufferStopBlockEntity> {
    override fun getBlockEntityClass(): Class<BufferStopBlockEntity> = BufferStopBlockEntity::class.java

    override fun getBlockEntityType(): BlockEntityType<out BufferStopBlockEntity> = GameContent.BUFFER_STOP_BE.get()
}
