package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.content.trains.entity.CarriageBogey
import com.simibubi.create.foundation.utility.VecHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.mixin.CarriageBogeyAccessor
import org.valkyrienskies.mod.common.util.toMinecraft

object MixinCarriageBogeyLogic {
    internal fun preUpdateCouplingAnchor(
        bogey: CarriageBogey,
        bogeySpacing: Int,
        partialTicks: Float,
        leading: Boolean,
        ci: CallbackInfo
    ) {
        val clientShip = MixinCarriageContraptionEntityRendererLogic.getClientShipForBogey(bogey) ?: return
        val carriageEntity = bogey.carriage.anyAvailableEntity()
        if (!CreateInteractiveUtil.isTrainDerailed(carriageEntity)) return

        bogey as CarriageBogeyAccessor

        val selfUpsideDown: Boolean = bogey.isUpsideDown
        val leadingUpsideDown: Boolean = bogey.carriage.leadingBogey().isUpsideDown
        var thisOffset: Vec3 = bogey.type.getConnectorAnchorOffset(selfUpsideDown)
        thisOffset = thisOffset.multiply(1.0, 1.0, (if (leading) -1 else 1).toDouble())

        thisOffset = VecHelper.rotate(thisOffset, bogey.pitch.getValue(partialTicks).toDouble(), Direction.Axis.X)
        thisOffset = VecHelper.rotate(thisOffset, bogey.yaw.getValue(partialTicks).toDouble(), Direction.Axis.Y)

        val carEntity = bogey.carriage.anyAvailableEntity()
        val bogeyPos = if (bogey.getIsLeading()) BlockPos.ZERO else BlockPos.ZERO.relative(
            carEntity.initialOrientation.counterClockWise, carEntity.carriage.bogeySpacing
        )

        thisOffset = thisOffset.add(bogeyPos.x.toDouble(), bogeyPos.y.toDouble(), bogeyPos.z.toDouble())

        if (selfUpsideDown != leadingUpsideDown) thisOffset =
            thisOffset.add(0.0, (if (selfUpsideDown) -2 else 2).toDouble(), 0.0)

        val shipPos = clientShip.getChunkClaimCenterPos(carriageEntity.level)
        val bogeyPosLocal = Vector3d(shipPos).add(0.5, 0.0, 0.5).add(thisOffset.x, thisOffset.y, thisOffset.z)

        bogey.couplingAnchors.set(leading, clientShip.renderTransform.shipToWorld.transformPosition(bogeyPosLocal, Vector3d()).toMinecraft())

        ci.cancel()
    }
}
