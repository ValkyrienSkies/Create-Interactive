package org.valkyrienskies.create_interactive.mixin_logic.client

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.simibubi.create.AllMovementBehaviours
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour
import com.simibubi.create.content.contraptions.render.ActorInstance
import com.simibubi.create.content.contraptions.render.FlwContraption
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour
import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.valkyrienskies.create_interactive.CreateActor
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
            val actor = contraption.getActorAt(blockPos)

            // Remove old instance, if one exists
            val oldActorInstance: ActorInstance? = actorToInstanceMap.remove(blockPos)
            if (oldActorInstance != null) {
                ((instanceWorld as ContraptionInstanceWorldAccessor).getBlockEntityInstanceManager() as ContraptionInstanceManagerDuck).`ci$deleteActorInstance`(
                    oldActorInstance
                )
            }
            try {
                if (actor != null) {
                    if (removeActorRendererInContraption(actor)) {
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
            if (removeActorRendererInContraption(actor)) {
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

    private fun removeActorRendererInContraption(actor: CreateActor): Boolean {
        val behaviour = AllMovementBehaviours.getBehaviour(actor.left.state)
        // Do not create actor render instances for deployers or mechanical bearings
        return behaviour is DeployerMovementBehaviour || behaviour is StabilizedBearingMovementBehaviour
    }
}
