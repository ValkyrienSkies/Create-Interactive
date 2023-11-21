package org.valkyrienskies.create_interactive.mixin_logic.client

import com.jozufozu.flywheel.backend.instancing.AbstractInstance
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
import com.simibubi.create.content.decoration.slidingDoor.SlidingDoorBlockEntity
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import org.joml.Vector3ic
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getChunkClaimCenterPos
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.shipIdToContraptionEntityClient
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.util.toBlockPos

internal object MixinInstanceManagerLogic {
    internal fun shouldRemoveBlockEntityInShip(blockEntity: BlockEntity): Boolean {
        val level = blockEntity.level
        val pos = blockEntity.blockPos
        val ship = level.getShipManagingPos(pos) ?: return false
        val contraptionEntityWeakReference = shipIdToContraptionEntityClient[ship.id] ?: return false
        val contraptionEntity = contraptionEntityWeakReference.get() ?: return false
        val shipCenter: Vector3ic = ship.getChunkClaimCenterPos(level!!)
        val relativePos = pos.subtract(shipCenter.toBlockPos())
        if (blockEntity is AbstractBogeyBlockEntity) {
            return (contraptionEntity.contraption as ContraptionDuck).`ci$hasBogeyAtPos`(relativePos)
        }
        return blockEntity !is DeployerBlockEntity
            && blockEntity !is SlidingDoorBlockEntity
            && blockEntity !is MechanicalBearingBlockEntity
            && (contraptionEntity.contraption as ContraptionDuck).`ci$hasActorAtPos`(relativePos)
    }

    internal fun preCreateInternal(obj: Any, cir: CallbackInfoReturnable<AbstractInstance?>) {
        if (obj is BlockEntity && shouldRemoveBlockEntityInShip(obj)) {
            // Don't create the instance
            cir.setReturnValue(null)
        }
    }
}
