package de.leoxian.moonlightcore.registry;


import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import de.leoxian.moonlightcore.util.nullness.Nullable;

public class LazyRegistryEntry<R, T extends R> implements NonnullSupplier<T> {

    private @Nullable NonnullSupplier<RegistryEntry<R, T>> candidate;
    private @Nullable RegistryEntry<R, T> entry = null;

    public LazyRegistryEntry(NonnullSupplier<RegistryEntry<R, T>> candidate) {
        this.candidate = candidate;
    }

    @Override
    public T get() {
        NonnullSupplier<RegistryEntry<R, T>> candidate = this.candidate;
        if(candidate != null) {
            this.entry = candidate.get();
            this.candidate = null;
        }

        return this.entry.get();
    }

}
