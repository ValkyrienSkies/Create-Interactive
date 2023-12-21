package org.valkyrienskies.create_interactive.content.buffer_stop

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider

class BufferStopRenderer(val context: BlockEntityRendererProvider.Context): SafeBlockEntityRenderer<BufferStopBlockEntity>() {
    override fun renderSafe(
        be: BufferStopBlockEntity, partialTicks: Float, ms: PoseStack?, buffer: MultiBufferSource,
        light: Int, overlay: Int
    ) {
        // TODO: Render here
    }
}
