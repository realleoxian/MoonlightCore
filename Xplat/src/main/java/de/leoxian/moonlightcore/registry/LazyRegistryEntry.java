package de.leoxian.moonlightcore.registry;

import de.leoxian.moonlightcore.util.nullness.Nullable;

import java.util.function.Supplier;

public class LazyRegistryEntry<R, T extends R> implements Supplier<T> {
    public @Nullable Supplier<RegistryEntry<R, T>> candidate;

    private @Nullable RegistryEntry<R, T> entry = null;

    public LazyRegistryEntry(Supplier<RegistryEntry<R, T>> candidate) {
        this.candidate = candidate;
    }

    @Override
    public T get() {
        if(candidate != null) {
            entry = candidate.get();
            candidate = null;
        }

        return entry.get();
    }
}
