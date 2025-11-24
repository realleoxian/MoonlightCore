package de.leoxian.moonlightcore.util.nullness;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface NonnullFunction<@Nonnull T, @Nonnull R> extends Function<T, R> {

    R apply(T t);

    default <V> NonnullFunction<T, V> andThen(NonnullFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }

}
