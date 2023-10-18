package org.valkyrienskies.create_interactive.forge.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.create_interactive.forge.DeferredRegisterImpl;
import org.valkyrienskies.create_interactive.registry.DeferredRegister;
import org.valkyrienskies.create_interactive.services.DeferredRegisterBackend;

public class DeferredRegisterBackendForge implements DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(
            @NotNull final String id,
            @NotNull final ResourceKey<Registry<T>> registry
    ) {
        return new DeferredRegisterImpl(id, registry);
    }
}
