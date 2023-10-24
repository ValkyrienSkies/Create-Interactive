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
        @JsonSchema(description = "Dummy config value")
        val maxConfig = 1
    }
}
