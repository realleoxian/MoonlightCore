/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 * Modifications by Leoxian, 2025
 */
package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.registry.DeferredRegistrar;
import de.leoxian.moonlightcore.registry.RegistryEntry;

public abstract class AbstractBuilder<R, T extends R, S extends EntryBuilder<R, T, S>> implements EntryBuilder<R, T, S> {
    private final DeferredRegistrar<R> registrar;
    private final String name;

    protected AbstractBuilder(DeferredRegistrar<R> registrar, String name) {
        this.registrar = registrar;
        this.name = name;
    }

    protected abstract T buildEntry();

    @Override
    public RegistryEntry<R, T> buildAndRegister() {
        return getRegistrar().register(name, this::buildEntry);
    }

    @Override
    public DeferredRegistrar<R> getRegistrar() {
        return this.registrar;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
