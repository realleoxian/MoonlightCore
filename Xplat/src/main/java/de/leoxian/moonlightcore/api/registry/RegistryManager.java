package de.leoxian.moonlightcore.api.registry;


import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Supplier;

public interface RegistryManager {

    <R> RegistryHelper<R> createHelper(ResourceKey<? extends Registry<R>> registryType, String namespace);

    <R, T extends R> DeferredObject<R, T> register(ResourceKey<? extends Registry<R>> registryType, ResourceLocation name, Function<ResourceLocation, T> func);

    default <R, T extends R> DeferredObject<R, T> register(ResourceKey<? extends Registry<R>> registryType, ResourceLocation name, Supplier<T> func) {
        return register(registryType, name, k -> func.get());
    }

}
