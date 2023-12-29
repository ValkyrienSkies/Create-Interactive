package org.valkyrienskies.create_interactive.mixin_logic.deployer

import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.mixin.deployer.DeployerBlockEntityAccessor
import org.valkyrienskies.create_interactive.mixinducks.DeployerDuck
import java.lang.reflect.Field

internal object MixinDeployerMovementBehaviourLogic {
    private var modeField: Field =
        DeployerBlockEntity::class.java.getDeclaredField("mode").apply { isAccessible = true }

    private fun getBlockEntity(context: MovementContext): DeployerBlockEntity? {
        return CreateInteractiveUtil.getBlockEntity(context) as? DeployerBlockEntity
    }

    internal fun preGetPlayer(context: MovementContext, cir: CallbackInfoReturnable<DeployerFakePlayer>) {
        val deployerBlockEntity = getBlockEntity(context) ?: return
        cir.setReturnValue(deployerBlockEntity.player)
    }

    internal fun preVisitNewPosition(context: MovementContext, ci: CallbackInfo) {
        val deployerBlockEntity = getBlockEntity(context) ?: return
        if ((deployerBlockEntity as DeployerDuck).`ci$getActorMode`().equals(DeployerActorMode.ACTOR_OFF)) {
            ci.cancel()
        }
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
