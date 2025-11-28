/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 */
package de.leoxian.moonlightcore.util.nullness;

import java.util.function.BiFunction;

@FunctionalInterface
public interface NonnullBiFunction<@Nonnull T, @Nonnull U, @Nonnull R> extends BiFunction<T, U, R> {

    R apply(T t, U u);

}
