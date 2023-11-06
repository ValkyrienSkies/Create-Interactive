package org.valkyrienskies.create_interactive.mixin_logic.deployer

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer
import net.minecraft.world.item.ItemStack
import org.joml.Vector3i
import org.joml.Vector3ic
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.getShipForMovementContext
import org.valkyrienskies.create_interactive.mixin.deployer.DeployerBlockEntityAccessor
import org.valkyrienskies.mod.common.yRange
import java.lang.reflect.Field

internal object MixinDeployerMovementBehaviourLogic {
    private var modeField: Field =
        DeployerBlockEntity::class.java.getDeclaredField("mode").apply { isAccessible = true }

    private fun getBlockEntity(context: MovementContext): DeployerBlockEntity? {
        val ship = getShipForMovementContext(context) ?: return null
        val shipCenter: Vector3ic = ship.chunkClaim.getCenterBlockCoordinates(context.world.yRange, Vector3i())
        val blockPos = context.localPos.offset(shipCenter.x(), shipCenter.y(), shipCenter.z())
        val blockEntity = context.world.getBlockEntity(blockPos)
        return blockEntity as? DeployerBlockEntity
    }

    internal fun preGetPlayer(context: MovementContext, cir: CallbackInfoReturnable<DeployerFakePlayer>) {
        val deployerBlockEntity = getBlockEntity(context) ?: return
        cir.setReturnValue(deployerBlockEntity.player)
    }

    internal fun preGetFilter(context: MovementContext, cir: CallbackInfoReturnable<ItemStack>) {
        val deployerBlockEntity = getBlockEntity(context) ?: return
        cir.setReturnValue((deployerBlockEntity as DeployerBlockEntityAccessor).getFiltering().filter)
    }

    internal fun preGetMode(context: MovementContext, cir: CallbackInfoReturnable<Any>) {
        val deployerBlockEntity = getBlockEntity(context) ?: return
        cir.setReturnValue(modeField.get(deployerBlockEntity))
    }
}
