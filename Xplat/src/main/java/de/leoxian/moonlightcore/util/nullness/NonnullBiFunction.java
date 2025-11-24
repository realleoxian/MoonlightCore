package de.leoxian.moonlightcore.util.nullness;

import java.util.function.BiFunction;

@FunctionalInterface
public interface NonnullBiFunction<@Nonnull T, @Nonnull U, @Nonnull R> extends BiFunction<T, U, R> {

    R apply(T t, U u);

}
