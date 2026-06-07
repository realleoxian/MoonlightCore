package de.realleoxian.moonlightcore.api.config.metadata;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record ConfigMetadataType<M, B>(Supplier<B> builderFactory, Function<B, M> initializer) {
    public M create(Consumer<B> builderModifier) {
        B builder = this.builderFactory().get();
        builderModifier.accept(builder);
        return this.initializer().apply(builder);
    }
}
