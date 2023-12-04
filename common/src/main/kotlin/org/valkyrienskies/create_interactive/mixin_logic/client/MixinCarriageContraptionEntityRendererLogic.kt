package org.valkyrienskies.create_interactive.mixin_logic.client

import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.trains.entity.CarriageBogey
import net.minecraft.client.multiplayer.ClientLevel
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toMinecraft

internal object MixinCarriageContraptionEntityRendererLogic {
    internal fun preTranslateBogey(
        ms: PoseStack,
        bogey: CarriageBogey,
    ) {
        val contraptionEntity = bogey.carriage.anyAvailableEntity()
        val shipId = (contraptionEntity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() ?: return
        val clientShip = (contraptionEntity.level as ClientLevel).shipObjectWorld.allShips.getById(shipId) ?: return
        if (CreateInteractiveUtil.isTrainDerailed(contraptionEntity)) {
            val rotationTransform = clientShip.renderTransform.shipToWorldRotation
            ms.translate(0.0, 0.5, 0.0)
            ms.mulPose(rotationTransform.toMinecraft())
            ms.translate(0.0, -0.5, 0.0)
        }
    }
}
