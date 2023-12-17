package org.valkyrienskies.create_interactive.mixin_logic.client

import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.trains.entity.CarriageBogey
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.mixin.CarriageBogeyAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinCarriageContraptionEntityRendererLogic {
    internal fun preTranslateBogey(
        ms: PoseStack,
        bogey: CarriageBogey,
        partialTicks: Float,
        ci: CallbackInfo,
    ) {
        val clientShip = getClientShipForBogey(bogey) ?: return
        val contraptionEntity = bogey.carriage.anyAvailableEntity()
        if (CreateInteractiveUtil.isTrainDerailed(contraptionEntity)) {
            val rotationTransform = clientShip.renderTransform.shipToWorldRotation
            translateBogeyWithShip(ms, bogey, partialTicks, rotationTransform)
            ci.cancel()
        }
    }

    private fun translateBogeyWithShip(
        ms: PoseStack, bogey: CarriageBogey, partialTicks: Float, rotation: Quaterniondc
    ) {
        bogey as CarriageBogeyAccessor

        val selfUpsideDown = bogey.isUpsideDown
        val leadingUpsideDown = bogey.carriage.leadingBogey().isUpsideDown

        val carEntity = bogey.carriage.anyAvailableEntity()
        val bogeyPos = if (bogey.getIsLeading()) BlockPos.ZERO else BlockPos.ZERO.relative(
            carEntity.initialOrientation.counterClockWise, carEntity.carriage.bogeySpacing
        )

        val offset: Vector3dc = bogeyPos.toJOMLD().rotate(rotation)

        TransformStack.cast(ms)
            // Add the bogey offset
            .translate(offset.x(), offset.y(), offset.z())
            // Add ship rotation
            .translate(0.0, 0.5, 0.0)
            .multiply(rotation.toMinecraft())
            .translate(0.0, -0.5, 0.0)
            // Regular create past here
            .rotateY(bogey.yaw.getValue(partialTicks).toDouble())
            .rotateX(bogey.pitch.getValue(partialTicks).toDouble())
            .translate(0.0, .5, 0.0)
            .rotateZ((if (selfUpsideDown) 180 else 0).toDouble())
            .translateY((if (selfUpsideDown != leadingUpsideDown) 2 else 0).toDouble())
    }

    internal fun getClientShipForBogey(bogey: CarriageBogey): ClientShip? {
        val contraptionEntity = bogey.carriage.anyAvailableEntity()
        val shipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return null
        return (contraptionEntity.level as ClientLevel).shipObjectWorld.allShips.getById(shipId)
    }
}
