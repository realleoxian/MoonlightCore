package de.leowgc.moonlightcore.api.registry;

import com.google.common.collect.ImmutableSet;
import de.leowgc.moonlightcore.api.util.nullness.NotnullSupplier;
import de.leowgc.moonlightcore.registry.RegistryHelperImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface RegistryHelper<R> {

    static <R> RegistryHelper<R> create(String modId, ResourceKey<? extends Registry<R>> registryType) {
        return RegistryHelperImpl.create(modId, registryType);
    }

    static <R> RegistryHelper<R> create(String modId, Registry<R> registryType) {
        return RegistryHelperImpl.create(modId, registryType.key());
    }

    <T extends R> RegistryEntry<R, T> register(String id, NotnullSupplier<T> valueSupplier);

    void bind();

    ImmutableSet<RegistryEntry<R, ?>> entries();

    ResourceKey<? extends Registry<R>> registryType();

}
