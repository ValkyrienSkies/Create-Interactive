package org.valkyrienskies.create_interactive.mixin_logic.client

import com.jozufozu.flywheel.backend.instancing.AbstractInstance
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import org.joml.Vector3i
import org.joml.Vector3ic
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.shipIdToContraptionEntityClient
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.yRange

internal object MixinInstanceManagerLogic {
    internal fun shouldRemoveBlockEntityInShip(blockEntity: BlockEntity): Boolean {
        val level = blockEntity.level
        val pos = blockEntity.blockPos
        val ship = level.getShipManagingPos(pos) ?: return false
        val contraptionEntityWeakReference = shipIdToContraptionEntityClient[ship.id] ?: return false
        val contraptionEntity = contraptionEntityWeakReference.get() ?: return false
        val shipCenter: Vector3ic = ship.chunkClaim.getCenterBlockCoordinates(level!!.yRange, Vector3i())
        val relativePos = pos.subtract(shipCenter.toBlockPos())
        return blockEntity !is DeployerBlockEntity && blockEntity !is SlidingDoorBlockEntity && (contraptionEntity.contraption as ContraptionDuck).`ci$hasActorAtPos`(
            relativePos,
            blockEntity is MechanicalBearingBlockEntity
        )
    }

    internal fun preCreateInternal(obj: Any, cir: CallbackInfoReturnable<AbstractInstance?>) {
        if (obj is BlockEntity && shouldRemoveBlockEntityInShip(obj)) {
            // Don't create the instance
            cir.setReturnValue(null)
        }
    }
}
