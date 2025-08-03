package de.leowgc.moonlightcore.api.util.nullness;

import java.util.function.Function;

@FunctionalInterface
public interface NotnullFunction<@NotnullType T, @NotnullType R> extends Function<T, R> {

    R apply(T t);

}
