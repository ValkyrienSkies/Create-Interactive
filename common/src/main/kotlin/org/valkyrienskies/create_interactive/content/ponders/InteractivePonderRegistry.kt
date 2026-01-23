package org.valkyrienskies.create_interactive.content.ponders

import com.simibubi.create.foundation.ponder.*
import com.tterrag.registrate.util.entry.ItemProviderEntry
import com.tterrag.registrate.util.entry.RegistryEntry
import net.createmod.ponder.api.registration.PonderPlugin
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper
import net.createmod.ponder.api.scene.SceneBuildingUtil
import net.createmod.ponder.api.scene.Selection
import net.createmod.ponder.foundation.registration.DefaultPonderSceneRegistrationHelper
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.valkyrienskies.create_interactive.CreateInteractiveMod
import org.valkyrienskies.create_interactive.GameContent
import org.valkyrienskies.create_interactive.content.ponders.scenes.InteractMe
import org.valkyrienskies.create_interactive.content.ponders.scenes.PropagatorBearings
import java.util.function.Function

class InteractivePonderRegistry : PonderPlugin {
    override fun getModId(): String {
        return CreateInteractiveMod.MOD_ID
    }

    override fun registerScenes(helper: PonderSceneRegistrationHelper<ResourceLocation?>) {
        val HELPER = helper.withKeyFunction { obj: RegistryEntry<*>? -> obj!!.getId() }
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

    }

    override fun registerTags(helper: PonderTagRegistrationHelper<ResourceLocation?>) {
        val HELPER = helper.withKeyFunction { obj: RegistryEntry<*>? -> obj!!.getId() }
        HELPER.registerTag(GameContent.INTERACT_ME.id).addToIndex().item(GameContent.INTERACT_ME.asItem()).register()
        HELPER.addToTag(GameContent.INTERACT_ME.id).add(GameContent.INTERACT_ME)
            .add(GameContent.INTERACT_ME_NOT)
            .add(GameContent.MECHANICAL_PROPAGATOR_BEARING_BLOCK)
            .add(GameContent.DISJOINTED_PROPAGATOR_BEARING_BLOCK)
    }

    companion object {
        fun getPonderLang(key: String): Component {
            return Component.translatable(CreateInteractiveMod.MOD_ID+".ponder."+key)
        }

        fun getNextLang(ponder: String, count: Int): String {
            return getPonderLang("$ponder.text_$count").string
        }


        fun BlockPos.selection(util: SceneBuildingUtil): Selection {
            return util.select().position(this)
        }
    }
}