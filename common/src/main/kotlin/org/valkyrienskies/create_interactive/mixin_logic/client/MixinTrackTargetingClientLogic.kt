package org.valkyrienskies.create_interactive.mixin_logic.client

import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.trains.graph.EdgePointType
import com.simibubi.create.content.trains.graph.TrackGraphLocation
import com.simibubi.create.content.trains.track.BezierTrackPointLocation
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour
import com.simibubi.create.content.trains.track.TrackTargetingBlockItem
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3dc
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinTrackTargetingClientLogic {
    /**
     * Fixed train station overlay not rendering on ships
     */
    internal fun render(
        lastHovered: BlockPos?,
        lastDirection: Boolean,
        lastType: EdgePointType<*>?,
        lastHoveredBezierSegment: BezierTrackPointLocation?,
        lastResult: TrackTargetingBlockItem.OverlapResult?,
        lastLocation: TrackGraphLocation?,
        ms: PoseStack,
        buffer: SuperRenderTypeBuffer?,
        camera: Vec3?,
        ci: CallbackInfo,
    ) {
        if (lastLocation == null || lastResult?.feedback != null || lastHovered == null || camera == null) return
        val mc = Minecraft.getInstance()
        val clientShip = mc.level.getShipManagingPos(lastHovered) as? ClientShip ?: return

        ci.cancel()

        val light = LevelRenderer.getLightColor(mc.level, lastHovered)
        val direction =
            if (lastDirection) Direction.AxisDirection.POSITIVE else Direction.AxisDirection.NEGATIVE
        val type =
            if (lastType === EdgePointType.SIGNAL) TrackTargetingBehaviour.RenderedTrackOverlayType.SIGNAL else if (lastType === EdgePointType.OBSERVER) TrackTargetingBehaviour.RenderedTrackOverlayType.OBSERVER else TrackTargetingBehaviour.RenderedTrackOverlayType.STATION
        ms.pushPose()

        val posInShip: Vector3dc = lastHovered.toJOMLD().add(0.5, 0.5, 0.5)
        val posInWorld: Vector3dc = clientShip.renderTransform.shipToWorld.transformPosition(posInShip, Vector3d())

        TransformStack.cast(ms)
            .translate(
                posInWorld.x() - camera.x(), posInWorld.y() - camera.y(), posInWorld.z() - camera.z()
            )
        val scale = clientShip.renderTransform.shipToWorldScaling.x().toFloat()
        ms.scale(scale, scale, scale)
        ms.mulPose(Quaternionf(clientShip.renderTransform.shipToWorldRotation))
        ms.translate(-0.5, -0.5, -0.5)
        TrackTargetingBehaviour.render(
            mc.level, lastHovered, direction, lastHoveredBezierSegment, ms, buffer, light,
            OverlayTexture.NO_OVERLAY, type, 1 + 1 / 16f
        )
        ms.popPose()
    }
}
