package org.valkyrienskies.create_interactive

import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.core.impl.hooks.VSEvents

object CreateInteractiveMod {
    const val MOD_ID = "create_interactive"

    @JvmStatic
    fun init() {
        VSConfigClass.registerConfig("create_interactive", CreateInteractiveConfig::class.java)
        registerCommonEvents()
        GameContent.init()
    }

    @JvmStatic
    fun initClient() {
        registerClientEvents()
    }

    private fun registerCommonEvents() {
        VSEvents.shipUnloadEventClient.on { (clientShip) -> CreateInteractiveUtil.onShipUnloadEventClient(clientShip) }
    }

    private fun registerClientEvents() {
        VSEvents.startUpdateRenderTransformsEvent.on { _ -> CreateInteractiveEventsClient.onStartUpdateRenderTransforms() }
    }
}
