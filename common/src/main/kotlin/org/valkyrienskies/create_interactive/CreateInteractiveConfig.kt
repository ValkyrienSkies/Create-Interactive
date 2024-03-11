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
