package org.valkyrienskies.create_interactive

import com.jozufozu.flywheel.core.PartialModel
import net.minecraft.resources.ResourceLocation

object CreateInteractivePartialModels {
    lateinit var BEARING_TOP_PROPAGATOR: PartialModel
        private set
    lateinit var BEARING_COG: PartialModel
        private set

    private fun block(path: String): PartialModel {
        return PartialModel(ResourceLocation(CreateInteractiveMod.MOD_ID, "block/$path"))
    }

    fun init() {
        println("Init")
        BEARING_TOP_PROPAGATOR = block("propagator_bearing_top")
        BEARING_COG = block("disjointed_propagator_bearing_cog")
    }
}
