package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.AllInteractionBehaviours
import com.simibubi.create.content.contraptions.behaviour.MovingInteractionBehaviour
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction
import com.simibubi.create.foundation.utility.AttachedRegistry
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

internal object MixinAllInteractionBehavioursLogic {
    internal fun getBehaviour(
        state: BlockState,
        blockBehaviors: AttachedRegistry<Block, MovingInteractionBehaviour>,
        globalBehaviors: List<AllInteractionBehaviours.BehaviourProvider>,
    ): MovingInteractionBehaviour? {
        var behaviour = blockBehaviors[state.block]
        if (behaviour != null) {
            return wrapGetBehavior(behaviour)
        }
        for (provider in globalBehaviors) {
            behaviour = provider.getBehaviour(state)
            if (behaviour != null) {
                return wrapGetBehavior(behaviour)
            }
        }
        return null
    }

    private fun wrapGetBehavior(behaviour: MovingInteractionBehaviour): MovingInteractionBehaviour? {
        return if (behaviour is SimpleBlockMovingInteraction || behaviour is DeployerMovingInteraction) {
            // Disable the dumb ones
            null
        } else behaviour
    }
}