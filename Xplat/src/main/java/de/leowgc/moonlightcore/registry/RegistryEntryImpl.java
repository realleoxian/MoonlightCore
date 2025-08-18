package de.leowgc.moonlightcore.registry;

import de.leowgc.moonlightcore.api.registry.RegistryEntry;
import de.leowgc.moonlightcore.api.util.nullness.NotnullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class RegistryEntryImpl<R, T extends R> implements RegistryEntry<R, T> {
    private final ResourceKey<? extends Registry<R>> registryType;
    private final ResourceLocation id;
    private final NotnullSupplier<T> valueCandidate;
    private T cachedValue;

    public RegistryEntryImpl(ResourceKey<? extends Registry<R>> registryType, ResourceLocation id, NotnullSupplier<T> valueCandidate) {
        this.registryType = registryType;
        this.id = id;
        this.valueCandidate = valueCandidate;
    }

    @Override
    public T get() {
        if(this.cachedValue == null) {
            this.cachedValue = this.valueCandidate.get();

            if(this.cachedValue == null) {
                throw new IllegalStateException("Value candidate for entry '%s' is null. Can't be cached".formatted(this.id));
            }
        }

        return this.cachedValue;
    }

    @Override
    public ResourceKey<? extends Registry<R>> registryType() {
        return this.registryType;
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }
}
