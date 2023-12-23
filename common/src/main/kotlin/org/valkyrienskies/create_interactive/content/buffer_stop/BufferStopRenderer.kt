package org.valkyrienskies.create_interactive.content.buffer_stop

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import org.valkyrienskies.create_interactive.services.NoOptimize

class BufferStopRenderer(val context: BlockEntityRendererProvider.Context): SafeBlockEntityRenderer<BlockEntity>() {
    @NoOptimize
    override fun renderSafe(
        be: BlockEntity, partialTicks: Float, ms: PoseStack?, buffer: MultiBufferSource,
        light: Int, overlay: Int
    ) {
        // TODO: Render here
    }
}
