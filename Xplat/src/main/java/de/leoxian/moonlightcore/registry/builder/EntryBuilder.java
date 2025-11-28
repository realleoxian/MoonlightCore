/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 * Modifications by Leoxian, 2025
 */
package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.registry.RegistryEntry;
import de.leoxian.moonlightcore.util.nullness.Nonnull;
import de.leoxian.moonlightcore.util.nullness.NonnullConsumer;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;

public interface EntryBuilder<R, T extends R, S extends EntryBuilder<R, T, S>> extends NonnullSupplier<RegistryEntry<R, T>> {

    RegistryEntry<R, T> buildAndRegister();

    DeferredRegistrar<R> getRegistrar();

    String getName();

    @SuppressWarnings("unchecked")
    default S onRegister(NonnullConsumer<T> callback) {
        getRegistrar().addRegisterCallback(getName(), callback);
        return (S) this;
    }

    default @Nonnull RegistryEntry<R, T> get() {
        return getRegistrar().getEntry(getName());
    }

    default @Nonnull T getValue() {
        return get().get();
    }

}
