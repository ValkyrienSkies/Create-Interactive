package org.valkyrienskies.create_interactive.mixin_logic

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction
import com.simibubi.create.content.kinetics.deployer.DeployerMovingInteraction

internal object MixinAllInteractionBehavioursLogic {
    internal fun postGetBehavior(behaviour: MovingInteractionBehaviour?): MovingInteractionBehaviour? {
        return if (behaviour is SimpleBlockMovingInteraction || behaviour is DeployerMovingInteraction) {
            // Disable the dumb ones
            null
        } else behaviour
    }
}
