package org.valkyrienskies.create_interactive.mixin_logic.client

import com.simibubi.create.content.contraptions.render.ActorInstance
import org.valkyrienskies.create_interactive.mixinducks.ActorInstanceDuck

internal object MixinContraptionInstanceManagerLogic {
    internal fun deleteActorInstance(actors: MutableList<ActorInstance>, actorInstance: ActorInstance) {
        actors.remove(actorInstance)
        val instanceDataList = (actorInstance as ActorInstanceDuck).`ci$getInstances`()
        for (instanceData in instanceDataList) {
            if (instanceData.isRemoved) continue
            instanceData.delete()
        }
    }
}
