package org.valkyrienskies.create_interactive.mixin_logic.client

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.simibubi.create.AllMovementBehaviours
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorInstance
import com.simibubi.create.content.contraptions.render.FlwContraption
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.apache.commons.lang3.tuple.MutablePair
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.doesContraptionHaveShipLoaded
import org.valkyrienskies.create_interactive.mixin.client.ContraptionInstanceWorldAccessor
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck
import org.valkyrienskies.create_interactive.mixinducks.ContraptionInstanceManagerDuck

internal object MixinFlwContraptionLogic {
    internal fun redirectBuildLayersGetRenderedBlocks(
        instance: Contraption,
        operation: Operation<Collection<StructureTemplate.StructureBlockInfo?>>
    ): Collection<StructureTemplate.StructureBlockInfo?> {
        // Only disable block rendering if the contraption has a ship
        return if (doesContraptionHaveShipLoaded(instance)) {
            emptyList()
        } else {
            operation.call(instance)
        }
    }

    internal fun preTick(instanceWorld: FlwContraption.ContraptionInstanceWorld, actorToInstanceMap: MutableMap<BlockPos, ActorInstance?>, contraption: Contraption) {
        for (blockPos in (contraption as ContraptionDuck).`ci$getChangedActors`()) {
            val actor: MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>? =
                contraption.getActorAt(blockPos)

            // Remove old instance, if one exists
            val oldActorInstance: ActorInstance? = actorToInstanceMap.remove(blockPos)
            if (oldActorInstance != null) {
                ((instanceWorld as ContraptionInstanceWorldAccessor).getBlockEntityInstanceManager() as ContraptionInstanceManagerDuck).`ci$deleteActorInstance`(
                    oldActorInstance
                )
            }
            try {
                if (actor != null) {
                    // Do not add deployers
                    if (AllMovementBehaviours.getBehaviour(actor.left.state) is DeployerMovementBehaviour) {
                        continue
                    }
                    // Add new instance
                    val actorInstance =
                        (instanceWorld as ContraptionInstanceWorldAccessor).getBlockEntityInstanceManager()
                            .createActor(actor)
                    actorToInstanceMap[actor.getLeft().pos] = actorInstance
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        (contraption as ContraptionDuck).`ci$clearChangedActors`()
    }

    internal fun preBuildActors(instanceWorld: FlwContraption.ContraptionInstanceWorld, actorToInstanceMap: MutableMap<BlockPos, ActorInstance?>, contraption: Contraption, ci: CallbackInfo) {
        for (actor in contraption.actors) {
            // Do not add deployers
            if (AllMovementBehaviours.getBehaviour(actor.left.state) is DeployerMovementBehaviour) {
                continue
            }
            try {
                val actorInstance =
                    (instanceWorld as ContraptionInstanceWorldAccessor).getBlockEntityInstanceManager()
                        .createActor(actor)
                actorToInstanceMap[actor.getLeft().pos] = actorInstance
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        ci.cancel()
    }

    internal fun preRenderStructureLayer(contraption: Contraption, ci: CallbackInfo) {
        if (doesContraptionHaveShipLoaded(contraption)) {
            ci.cancel()
        }
    }

    internal fun preBuildInstancedBlockEntities(contraption: Contraption, ci: CallbackInfo) {
        if (doesContraptionHaveShipLoaded(contraption)) {
            ci.cancel()
        }
    }
}
