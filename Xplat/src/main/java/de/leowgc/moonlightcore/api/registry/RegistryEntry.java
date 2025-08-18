package de.leowgc.moonlightcore.api.registry;

import de.leowgc.moonlightcore.api.util.nullness.NotnullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface RegistryEntry<R, T extends R> extends NotnullSupplier<T> {

    @Override
    T get();

    ResourceKey<? extends Registry<R>> registryType();

    ResourceLocation id();

}
