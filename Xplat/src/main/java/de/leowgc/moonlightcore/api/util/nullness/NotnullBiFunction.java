package de.leowgc.moonlightcore.api.util.nullness;

import java.util.function.BiFunction;

@FunctionalInterface
public interface NotnullBiFunction<@NotnullType T , @NotnullType U, @NotnullType R> extends BiFunction<T, U, R> {

    R apply(T t, U u);

}
