package de.leowgc.moonlightcore.api.util.nullness;

@FunctionalInterface
public interface NotnullTriConsumer<T, U, V> {

    void accept(T t, U u, V v);

}
