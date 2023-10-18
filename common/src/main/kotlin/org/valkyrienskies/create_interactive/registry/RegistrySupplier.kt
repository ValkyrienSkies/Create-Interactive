package org.valkyrienskies.create_interactive.registry

interface RegistrySupplier<T> {

    val name: String
    fun get(): T
}
