package de.leoxian.moonlightcore.util.nullness;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface NonnullBiConsumer<@Nonnull T, @Nonnull U> extends BiConsumer<T, U> {

    void accept(T t, U u);

}
