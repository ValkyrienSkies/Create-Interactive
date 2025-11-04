package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.content.contraptions.render.ActorVisual
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck

internal object MixinContraptionInstanceManagerLogic {
    internal fun deleteActorInstance(actors: MutableList<ActorVisual>, actorInstance: ActorVisual) {
        actors.remove(actorInstance)
        val instanceDataList = (actorInstance as ActorInstanceDuck).`ci$getInstances`()
        for (instanceData in instanceDataList) {
            instanceData.delete()
        }
    }
}
