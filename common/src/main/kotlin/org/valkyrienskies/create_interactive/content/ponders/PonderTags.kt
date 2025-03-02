package org.valkyrienskies.create_interactive.content.ponders

import com.simibubi.create.Create
import com.simibubi.create.foundation.ponder.PonderRegistry
import com.simibubi.create.foundation.ponder.PonderTag
import org.valkyrienskies.create_interactive.GameContent

class PonderTags {
    companion object {
        val INTERACTIVE_PONDERS: PonderTag = create("interactive_ponders").item(GameContent.INTERACT_ME.get())
            .defaultLang("Create: Interactive", "Mechanics and features of Create: Interactive")
            .addToIndex()

        private fun create(id: String): PonderTag {
            return PonderTag(Create.asResource(id))
        }

        fun register() {
            PonderRegistry.TAGS.forTag(INTERACTIVE_PONDERS)
                .add(GameContent.INTERACT_ME.getId())
                .add(GameContent.INTERACT_ME_NOT.getId())
                .add(GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK.getId())
        }
    }
}