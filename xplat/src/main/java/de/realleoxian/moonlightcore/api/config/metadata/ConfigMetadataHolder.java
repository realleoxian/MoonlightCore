package de.realleoxian.moonlightcore.api.config.metadata;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Map;

@ApiStatus.NonExtendable
public interface ConfigMetadataHolder {
    @Nullable
    <M> M getMetadata(ConfigMetadataType<M, ?> type);

    <M> boolean hasMetadata(ConfigMetadataType<M, ?> type);

    @UnmodifiableView
    Map<ConfigMetadataType<?, ?>, ?> getMetadata();
}
