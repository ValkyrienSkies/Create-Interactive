package org.valkyrienskies.create_interactive

import dev.engine_room.flywheel.lib.model.baked.PartialModel
import net.minecraft.resources.ResourceLocation

object CreateInteractivePartialModels {
    lateinit var BEARING_TOP_PROPAGATOR: PartialModel
        private set
    lateinit var BEARING_TOP_PROPAGATOR_DISJOINTED: PartialModel
        private set
    lateinit var BEARING_COG: PartialModel
        private set

    private fun block(path: String): PartialModel {
        return PartialModel.of(ResourceLocation(CreateInteractiveMod.MOD_ID, "block/$path"))
    }

    fun init() {
        //println("Init")
        BEARING_TOP_PROPAGATOR = block("propagator_bearing_top")
        BEARING_TOP_PROPAGATOR_DISJOINTED = block("disjointed_propagator_bearing_top")
        BEARING_COG = block("disjointed_propagator_bearing_cog")
    }
}
