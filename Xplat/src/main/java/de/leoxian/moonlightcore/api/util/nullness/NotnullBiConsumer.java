package de.leoxian.moonlightcore.api.util.nullness;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface NotnullBiConsumer<@NotnullType T, @NotnullType U> extends BiConsumer<T, U> {

    void accept(T t, U u);

}
