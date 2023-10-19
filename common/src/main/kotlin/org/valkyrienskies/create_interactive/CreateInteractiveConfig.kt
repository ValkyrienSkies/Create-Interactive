package org.valkyrienskies.create_interactive

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object CreateInteractiveConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client

    class Server {
        @JsonSchema(description = "The config yeah")
        val maxConfig = 1
    }
}
