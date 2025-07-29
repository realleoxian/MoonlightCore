package de.leoxian.moonlightcore.api.util.nullness;

import java.util.function.Consumer;

@FunctionalInterface
public interface NotnullConsumer<@NotnullType T> extends Consumer<T> {

    void accept(T t);

}
