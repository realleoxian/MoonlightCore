package de.realleoxian.moonlightcore.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Supplier;

public interface RegistryHelper {
    <R> Registrar<R> registrar(ResourceKey<? extends Registry<R>> registryType);

    <R> Supplier<Registry<R>> getRegistry(ResourceKey<? extends Registry<R>> registryType);

    interface Registrar<R> {
        <T extends R> DeferredObject<R, T> register(String name, Function<ResourceLocation, T> func);

        default <T extends R> DeferredObject<R, T> register(String name, Supplier<T> sup) {
            return register(name, k -> sup.get());
        }
    }
}
