package org.valkyrienskies.create_interactive.content.ponders

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper
import org.valkyrienskies.create_interactive.CreateInteractiveMod
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.content.ponders.scenes.PropagatorBearings

class PonderRegistry {
    companion object {
        val HELPER: PonderRegistrationHelper = PonderRegistrationHelper(CreateInteractiveMod.MOD_ID)

        fun register() {
            HELPER.forComponents(
                GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK,
                GameContent.DISJOINTED_PROPAGATOR_BEARING_BLOCK
            )
            .addStoryBoard("propagator_bearing", PropagatorBearings::standardBearing);

        }
    }
}