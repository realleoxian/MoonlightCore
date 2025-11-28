/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 */
package de.leoxian.moonlightcore.util.nullness;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface NonnullBiConsumer<@Nonnull T, @Nonnull U> extends BiConsumer<T, U> {

    void accept(T t, U u);

}
