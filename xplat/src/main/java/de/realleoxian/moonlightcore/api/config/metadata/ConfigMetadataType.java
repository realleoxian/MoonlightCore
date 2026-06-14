package de.realleoxian.moonlightcore.api.config.metadata;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record ConfigMetadataType<M, B>(Supplier<B> builderFactory, Function<B, M> metadataFactory, boolean inherit) {
    public M make(Consumer<B> func) {
        final var builder = builderFactory().get();
        func.accept(builder);
        return metadataFactory.apply(builder);
    }
}
