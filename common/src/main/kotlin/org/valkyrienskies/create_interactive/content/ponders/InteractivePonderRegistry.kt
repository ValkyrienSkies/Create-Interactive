package org.valkyrienskies.create_interactive.content.ponders

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper
import com.simibubi.create.foundation.ponder.PonderRegistry
import com.simibubi.create.foundation.ponder.PonderTag
import net.minecraft.network.chat.Component
import org.valkyrienskies.create_interactive.CreateInteractiveMod
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.content.ponders.scenes.PropagatorBearings

class InteractivePonderRegistry {
    companion object {
        val HELPER: PonderRegistrationHelper = PonderRegistrationHelper(CreateInteractiveMod.MOD_ID)
        val TAG: PonderTag = HELPER.createTag("ponders")
            .item(GameContent.INTERACT_ME.asItem())
            // "Create: Interactive"
            // "Mechanics and features of Create: Interactive"
            .defaultLang(getPonderLang("tag.ponders").string, getPonderLang("tag.ponders.description").string)
            .addToIndex()

        fun register() {
            HELPER.forComponents(
                GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK,
                GameContent.DISJOINTED_PROPAGATOR_BEARING_BLOCK
            )
            .addStoryBoard("propagator_bearing", PropagatorBearings::standardBearing)
            .addStoryBoard("disjointed_bearing", PropagatorBearings::disjointedBearing)

            PonderRegistry.TAGS.forTag(TAG)
                .add(GameContent.INTERACT_ME)
                .add(GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK)
                .add(GameContent.DISJOINTED_PROPAGATOR_BEARING_BLOCK)

        }

        fun getPonderLang(key: String): Component {
            return Component.translatable(CreateInteractiveMod.MOD_ID+".ponder."+key)
        }
    }
}