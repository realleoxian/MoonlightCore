package de.leowgc.moonlightcore.api.util.nullness;

import java.util.function.Supplier;

@FunctionalInterface
public interface NotnullSupplier<@NotnullType T> extends Supplier<T> {

    T get();

}
