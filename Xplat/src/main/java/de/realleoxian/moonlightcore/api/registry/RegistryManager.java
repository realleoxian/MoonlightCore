package de.realleoxian.moonlightcore.api.registry;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Supplier;

public interface RegistryManager {

    static RegistryManager get() {
        return MoonlightCore.getRegistryManager();
    }

    <R> RegistryHelper<R> createHelper(ResourceKey<? extends Registry<R>> registryType, String namespace);

    <R, T extends R> DeferredObject<R, T> register(ResourceKey<? extends Registry<R>> registryType, ResourceLocation name, Function<ResourceLocation, T> func);

    <R> Supplier<Registry<R>> getRegistry(ResourceKey<? extends Registry<R>> registryType);

    default <R, T extends R> DeferredObject<R, T> register(ResourceKey<? extends Registry<R>> registryType, ResourceLocation name, Supplier<T> func) {
        return register(registryType, name, k -> func.get());
    }

}
