package org.valkyrienskies.create_interactive

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Matrix4f
import com.simibubi.create.foundation.gui.AllIcons
import com.simibubi.create.foundation.gui.element.DelegatedStencilElement
import com.simibubi.create.foundation.utility.Color
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.create_interactive.services.NoOptimize

class CreateInteractiveIcons(var x: Int, var y: Int) : AllIcons(x, y) {
    private var iconX = 0
    private var iconY = 0

    init {
        iconX = x * 16
        iconY = y * 16
    }

    private operator fun next(): CreateInteractiveIcons {
        return CreateInteractiveIcons(++this.x, this.y)
    }

    private fun newRow(): CreateInteractiveIcons {
        return CreateInteractiveIcons(0.also { this.x = 0 }, ++this.y)
    }

    @Environment(EnvType.CLIENT)
    @NoOptimize
    override fun bind() {
        RenderSystem.setShaderTexture(0, ICON_ATLAS)
    }

    @Environment(EnvType.CLIENT)
    @NoOptimize
    override fun render(matrixStack: PoseStack?, x: Int, y: Int) {
        bind()
        GuiComponent.blit(matrixStack, x, y, 0, iconX.toFloat(), iconY.toFloat(), 16, 16, 256, 256)
    }

    @Environment(EnvType.CLIENT)
    @NoOptimize
    override fun render(matrixStack: PoseStack?, x: Int, y: Int, component: GuiComponent) {
        bind()
        component.blit(matrixStack, x, y, iconX, iconY, 16, 16)
    }
    @Environment(EnvType.CLIENT)
    @NoOptimize
    override fun render(ms: PoseStack, buffer: MultiBufferSource, color: Int) {
        val builder = buffer.getBuffer(RenderType.text(ICON_ATLAS))
        val matrix = ms.last().pose()
        val rgb = Color(color)
        val light = LightTexture.FULL_BRIGHT
        val vec1 = Vec3(0.0, 0.0, 0.0)
        val vec2 = Vec3(0.0, 1.0, 0.0)
        val vec3 = Vec3(1.0, 1.0, 0.0)
        val vec4 = Vec3(1.0, 0.0, 0.0)
        val u1: Float = iconX * 1f / ICON_ATLAS_SIZE
        val u2: Float = (iconX + 16) * 1f / ICON_ATLAS_SIZE
        val v1: Float = iconY * 1f / ICON_ATLAS_SIZE
        val v2: Float = (iconY + 16) * 1f / ICON_ATLAS_SIZE
        vertex(builder, matrix, vec1, rgb, u1, v1, light)
        vertex(builder, matrix, vec2, rgb, u1, v2, light)
        vertex(builder, matrix, vec3, rgb, u2, v2, light)
        vertex(builder, matrix, vec4, rgb, u2, v1, light)
    }

    @Environment(EnvType.CLIENT)
    private fun vertex(
        builder: VertexConsumer,
        matrix: Matrix4f,
        vec: Vec3,
        rgb: Color,
        u: Float,
        v: Float,
        light: Int
    ) {
        builder.vertex(matrix, vec.x.toFloat(), vec.y.toFloat(), vec.z.toFloat())
            .color(rgb.red, rgb.green, rgb.blue, 255)
            .uv(u, v)
            .uv2(light)
            .endVertex()
    }

    @Environment(EnvType.CLIENT)
    @NoOptimize
    override fun asStencil(): DelegatedStencilElement {
        return DelegatedStencilElement().withStencilRenderer<DelegatedStencilElement> { ms: PoseStack, w: Int, h: Int, alpha: Float ->
            this.render(
                ms,
                0,
                0
            )
        }.withBounds(16, 16)
    }

    companion object {
        val INSTANCE = CreateInteractiveIcons(0, -1)
        val ICON_ATLAS = CreateInteractiveMod.asResource("textures/gui/icons.png")
        val ICON_ATLAS_SIZE = 256

        val I_COMEDY = INSTANCE.newRow()
        val I_TRAGEDY = INSTANCE.next()
    }
}
