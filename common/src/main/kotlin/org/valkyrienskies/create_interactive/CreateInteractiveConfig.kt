package org.valkyrienskies.create_interactive

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object CreateInteractiveConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client {
        @JsonSchema(description = "Dummy config value")
        val maxConfig = 1
    }

    class Server {
        @JsonSchema(description = "If false, requires contraptions to have the \"Interact Me\" block for it to be Interactive")
        // If someone knows a better way of making this config option come up first other than adding 'a', please do replace this
        val aInteractiveByDefault = false

        @JsonSchema(description = "Make Translating Contraptions Interactive (Piston, Gantry, Pulley)")
        val enableTranslating = true

        @JsonSchema(description = "Make Trains Interactive")
        val enableTrain = true

        @JsonSchema(description = "Make Bearing Contraptions Interactive")
        val enableBearing = true

        @JsonSchema(description = "Make Clockwork Contraptions Interactive")
        val enableClockwork = true

        @JsonSchema(description = "Make Mounted Contraptions Interactive (Minecart)")
        val enableMounted = true
    }
}
