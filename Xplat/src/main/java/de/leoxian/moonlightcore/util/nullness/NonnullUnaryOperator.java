/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 */
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
