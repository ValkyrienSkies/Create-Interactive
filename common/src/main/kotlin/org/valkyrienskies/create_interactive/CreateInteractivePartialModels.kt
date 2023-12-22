package org.valkyrienskies.create_interactive

import com.jozufozu.flywheel.core.PartialModel
import net.minecraft.resources.ResourceLocation

object CreateInteractivePartialModels {
    val BEARING_TOP_PROPAGATOR = block("propagator_bearing_top")
    val BEARING_COG = block("disjointed_propagator_bearing_cog")

    private fun block(path: String): PartialModel {
        return PartialModel(ResourceLocation(CreateInteractiveMod.MOD_ID, "block/$path"))
    }

    fun init() {
        println("Init")
    }
}
