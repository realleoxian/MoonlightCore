/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 */
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
