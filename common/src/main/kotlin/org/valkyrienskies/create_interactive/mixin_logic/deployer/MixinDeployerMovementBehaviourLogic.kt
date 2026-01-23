package org.valkyrienskies.create_interactive.mixin_logic.deployer

import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateInteractiveUtil
import org.valkyrienskies.create_interactive.mixin.deployer.DeployerBlockEntityAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.create_interactive.mixinducks.DeployerDuck
import java.lang.reflect.Field

internal object MixinDeployerMovementBehaviourLogic {
    private var modeField: Field =
        DeployerBlockEntity::class.java.getDeclaredField("mode").apply { isAccessible = true }

    private fun getBlockEntity(context: MovementContext): DeployerBlockEntity? {
        return CreateInteractiveUtil.getBlockEntity(context) as? DeployerBlockEntity
    }

    private fun check(context: MovementContext) : Boolean {
        val entity = context.contraption.entity
        return entity is AbstractContraptionEntityDuck && entity.`ci$getShadowShipId`() != null
    }

    internal fun preGetPlayer(context: MovementContext, cir: CallbackInfoReturnable<DeployerFakePlayer>) {
        if (check(context)) {
            val deployerBlockEntity = getBlockEntity(context) ?: return
            cir.setReturnValue(deployerBlockEntity.player)
        }
    }

    internal fun preVisitNewPosition(context: MovementContext, ci: CallbackInfo) {
        if (check(context)) {
            val deployerBlockEntity = getBlockEntity(context) ?: return
            if ((deployerBlockEntity as DeployerDuck).`ci$getActorMode`().equals(DeployerActorMode.ACTOR_OFF)) {
                ci.cancel()
            }
        }
    }

    internal fun preGetFilter(context: MovementContext, cir: CallbackInfoReturnable<ItemStack>) {
        if (check(context)) {
            val deployerBlockEntity = getBlockEntity(context) ?: return
            cir.setReturnValue((deployerBlockEntity as DeployerBlockEntityAccessor).getFiltering().filter)
        }
    }

    internal fun preGetMode(context: MovementContext, cir: CallbackInfoReturnable<Any>) {
        if (check(context)) {
            val deployerBlockEntity = getBlockEntity(context) ?: return
            cir.setReturnValue(modeField.get(deployerBlockEntity))
        }
    }

    internal fun preCreateVisual(contraption: Contraption, cir: CallbackInfoReturnable<ActorVisual>) {
        if ((contraption.entity as AbstractContraptionEntityDuck).`ci$getShadowShipId`() != null) {
            cir.returnValue = null
        }
    }
}
