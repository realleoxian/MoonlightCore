package de.realleoxian.moonlightcore.api.config.metadata;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface ConfigMetadataHolder {
    @Nullable
    <M> M getMetadata(ConfigMetadataType<M, ?> metadataType);

    boolean hasMetadata(ConfigMetadataType<?, ?> metadataType);

    interface Builder<S extends Builder<S>> {
        <M, B> S metadata(ConfigMetadataType<M, B> metadataType, Consumer<B> builder);
    }
}
