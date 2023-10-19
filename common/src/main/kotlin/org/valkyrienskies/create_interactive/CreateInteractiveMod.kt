package org.valkyrienskies.create_interactive

import org.valkyrienskies.core.impl.config.VSConfigClass

object CreateInteractiveMod {
    const val MOD_ID = "create_interactive"

    @JvmStatic
    fun init() {
        VSConfigClass.registerConfig("create_interactive", CreateInteractiveConfig::class.java)
    }

    @JvmStatic
    fun initClient() {

    }
}
