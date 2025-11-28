/*
 * This source code file is subject to the terms of the Mozilla Public License, v. 2.0.
 * Based on code from Tterrag1098's Registrate (https://github.com/tterrag1098/Registrate).
 */
package de.leoxian.moonlightcore.util.nullness;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface NonnullConsumer<@Nonnull T> extends Consumer<T> {

    void accept(T t);

    default NonnullConsumer<T> andThen(NonnullConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return t -> {
            accept(t);
            after.accept(t);
        };
    }

}
