package org.valkyrienskies.create_interactive

import com.simibubi.create.AllMovementBehaviours
import com.simibubi.create.content.contraptions.bearing.StabilizedBearingMovementBehaviour
import com.simibubi.create.content.contraptions.behaviour.MovementContext
import com.simibubi.create.content.kinetics.deployer.DeployerMovementBehaviour
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.apache.commons.lang3.tuple.MutablePair

object CreateInteractiveUtilClient {
    internal fun removeActorRendererInContraption(
        actor: MutablePair<StructureTemplate.StructureBlockInfo, MovementContext>
    ): Boolean {
        val behaviour = AllMovementBehaviours.getBehaviour(actor.left.state)
        // Do not create actor render instances for deployers or mechanical bearings
        return behaviour is DeployerMovementBehaviour || behaviour is StabilizedBearingMovementBehaviour
    }
}