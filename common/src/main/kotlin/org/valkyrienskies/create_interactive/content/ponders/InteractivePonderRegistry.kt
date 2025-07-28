package org.valkyrienskies.create_interactive.content.ponders

import com.simibubi.create.foundation.ponder.*
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import org.valkyrienskies.create_interactive.CreateInteractiveMod
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.content.ponders.scenes.InteractMe
import org.valkyrienskies.create_interactive.content.ponders.scenes.PropagatorBearings

class InteractivePonderRegistry {
    companion object {
        val HELPER: PonderRegistrationHelper = PonderRegistrationHelper(CreateInteractiveMod.MOD_ID)
        val TAG: PonderTag = HELPER.createTag("ponders")
            .item(GameContent.INTERACT_ME.asItem())

            // This Lang is NOT default. It secretly does forced datagen behind the scenes.
            // Without it, the tag ISN'T REGISTERED, what the fuck create
            // OH WAIT, on Fabric it ISN'T datagenned, so you still need it in the lang ANYWAY :SOB:
            .defaultLang("Create: Interactive", "Mechanics and features of Create: Interactive")
            .addToIndex()

        fun register() {
            HELPER.forComponents(
                GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK,
                GameContent.DISJOINTED_PROPAGATOR_BEARING_BLOCK
            )
            .addStoryBoard("propagator_bearing", PropagatorBearings::standardBearing)
            .addStoryBoard("disjointed_bearing", PropagatorBearings::disjointedBearing)

            HELPER.forComponents(
                GameContent.INTERACT_ME,
                GameContent.INTERACT_ME_NOT
            )
            .addStoryBoard("normal_sticker", InteractMe::normalSticker)
            .addStoryBoard("inverted_sticker", InteractMe::invertedSticker)

            PonderRegistry.TAGS.forTag(TAG)
                .add(GameContent.INTERACT_ME)
                .add(GameContent.INTERACT_ME_NOT)
                .add(GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK)
                .add(GameContent.DISJOINTED_PROPAGATOR_BEARING_BLOCK)
        }

        fun getPonderLang(key: String): Component {
            return Component.translatable(CreateInteractiveMod.MOD_ID+".ponder."+key)
        }

        fun getNextLang(ponder: String, count: Int): String {
            return getPonderLang("$ponder.text_$count").string
        }


        fun BlockPos.selection(util: SceneBuildingUtil): Selection {
            return util.select.position(this)
        }
    }
}