package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.api.behaviour.movement.MovementBehaviour
import com.simibubi.create.content.contraptions.Contraption
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.contraptions.render.ActorVisual
import com.simibubi.create.content.contraptions.render.ClientContraption
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.apache.commons.lang3.tuple.MutablePair
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import org.valkyrienskies.create_interactive.CreateActor
import org.valkyrienskies.create_interactive.CreateInteractiveUtil.doesContraptionHaveShipLoaded
import org.valkyrienskies.create_interactive.mixin.client.ContraptionInstanceWorldAccessor
import org.valkyrienskies.create_interactive.mixinducks.AbstractContraptionEntityDuck
import org.valkyrienskies.create_interactive.mixinducks.ContraptionDuck
import org.valkyrienskies.create_interactive.mixinducks.ContraptionInstanceManagerDuck
import java.util.BitSet

internal object MixinClientContraptionLogic {
    internal fun wrapGetRenderedBlocks(
        contraption: Contraption,
        cir: CallbackInfoReturnable<ClientContraption.RenderedBlocks>
    ){
        // Only disable block rendering if the contraption has a ship
        if (doesContraptionHaveShipLoaded(contraption)) {
            cir.returnValue = ClientContraption.RenderedBlocks({ _ -> Blocks.AIR.defaultBlockState() }, listOf())
        }
    }

    internal fun preTick(instanceWorld: VirtualRenderWorld, actorToInstanceMap: MutableMap<BlockPos, ActorVisual?>, contraption: Contraption) {
        val entity = contraption.entity
        if (entity is AbstractContraptionEntityDuck && entity.`ci$getShadowShipId`() == null) { return }

        for (blockPos in (contraption as ContraptionDuck).`ci$getChangedActors`()) {
            val actor = contraption.getActorAt(blockPos)

            // Remove old instance, if one exists
            val oldActorInstance: ActorVisual? = actorToInstanceMap.remove(blockPos)
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
                    //val actorInstance = MovementBehaviour.REGISTRY.get(instanceWorld.getBlockState(blockPos))?.createVisual()
                    //actorToInstanceMap[actor.getLeft().pos] = actorInstance
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        (contraption as ContraptionDuck).`ci$clearChangedActors`()
    }

    internal fun preBuildActors(instanceWorld: VirtualRenderWorld, actorToInstanceMap: MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>, contraption: Contraption, ci: CallbackInfo) {
        val entity = contraption.entity
        if (entity is AbstractContraptionEntityDuck && entity.`ci$getShadowShipId`() == null) { return }

        for (actor in contraption.actors) {
            if (removeActorRendererInContraption(actor)) {
                continue
            }
            try {
                //val actorInstance = MovementBehaviour.REGISTRY.get(actorToInstanceMap.left.state)?.createVisual()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        ci.cancel()
    }

    internal fun preSetupVisualizer(contraption: Contraption, ci: CallbackInfo){
        if (doesContraptionHaveShipLoaded(contraption)) ci.cancel()
    }

    internal fun preSetupStructure(contraption: Contraption, cir: CallbackInfo) {
        if (doesContraptionHaveShipLoaded(contraption)) {
            cir.cancel()
        }
    }

    internal fun postGetShouldRender(contraption: Contraption, cir: CallbackInfoReturnable<BitSet>) {
        if (doesContraptionHaveShipLoaded(contraption)) {
            cir.returnValue = BitSet(cir.returnValue.length())
        }
    }

    private fun removeActorRendererInContraption(actor: CreateActor): Boolean {
        val behaviour = MovementBehaviour.REGISTRY.get(actor.left.state)
        // Do not create actor render instances for deployers or mechanical bearings
        return behaviour is DeployerMovementBehaviour || behaviour is StabilizedBearingMovementBehaviour
    }
}
