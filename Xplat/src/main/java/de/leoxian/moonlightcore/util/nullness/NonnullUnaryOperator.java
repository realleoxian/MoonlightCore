package de.leoxian.moonlightcore.util.nullness;

import java.util.Objects;

@FunctionalInterface
public interface NonnullUnaryOperator<@Nonnull T> extends NonnullFunction<T, T> {

    static <T> NonnullUnaryOperator<T> identity() {
        return t -> t;
    }

    default <V> NonnullUnaryOperator<T> andThen(NonnullUnaryOperator<T> after) {
        Objects.requireNonNull(after);
        return t -> after.apply(apply(t));
    }

}
