package de.realleoxian.moonlightcore.xplat.config.schema;

import com.google.common.collect.ImmutableMap;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataType;
import de.realleoxian.moonlightcore.api.config.schema.ConfigKey;
import de.realleoxian.moonlightcore.api.config.schema.ConfigProperty;
import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import de.realleoxian.moonlightcore.api.config.schema.RestartType;
import de.realleoxian.moonlightcore.api.config.schema.validator.ConfigPropertyValidator;
import de.realleoxian.moonlightcore.xplat.config.ModConfigImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public sealed class ConfigPropertyImpl<T> implements ConfigProperty<T> permits ListConfigPropertyImpl {
    private final ConfigKey key;
    private final ConfigPropertyType<T> type;
    private final ConfigPropertyValidator<T> validator;
    private final Supplier<T> defaultValue;
    private final RestartType restartType;
    private final Map<ConfigMetadataType<?, ?>, Object> metadata;

    private volatile T value;

    ConfigPropertyImpl(ConfigPropertyImpl.Builder<T> builder) {
        this.key = builder.key;
        this.type = builder.type;
        this.validator = builder.validator;
        this.defaultValue = builder.defaultValue;
        this.restartType = builder.restartType;
        this.metadata = Collections.unmodifiableMap(builder.metadata);
    }

    @ApiStatus.Internal
    void validate(LoadedConfig loadedConfig, ModConfigImpl config) {
        config.lock.lock();
        this.value = loadedConfig.getRaw(this);
        if (!this.validator.test(value)) {
            this.value = this.defaultValue.get();
        }
        config.lock.unlock();
    }

    @Override
    public T value() {
        if (this.value == null) {
            this.value = this.defaultValue.get();
        }
        return this.value;
    }

    @Override
    public ConfigKey key() {
        return this.key;
    }

    @Override
    public ConfigPropertyType<T> type() {
        return this.type;
    }

    @Override
    public ConfigPropertyValidator<T> validator() {
        return this.validator;
    }

    @Override
    public Supplier<T> defaultValue() {
        return this.defaultValue;
    }

    @Override
    public RestartType restartType() {
        return this.restartType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <M> M getMetadata(ConfigMetadataType<M, ?> metadataType) {
        return (M) this.metadata.get(metadataType);
    }

    @Override
    public boolean hasMetadata(ConfigMetadataType<?, ?> metadataType) {
        return this.metadata.containsKey(metadataType);
    }

    public static final class Builder<T> implements ConfigProperty.Builder<T> {
        private final ConfigKey key;
        private final ConfigPropertyType<T> type;
        private final Supplier<T> defaultValue;

        private final Map<ConfigMetadataType<?, ?>, Object> metadata = new IdentityHashMap<>();
        @SuppressWarnings("unchecked")
        private ConfigPropertyValidator<T> validator = (ConfigPropertyValidator<T>) ConfigPropertyValidator.NO_OP;
        private RestartType restartType = RestartType.NONE;

        Builder(ConfigKey key, ConfigPropertyType<T> type, Supplier<T> defaultValue) {
            this.key = key;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        @Override
        public ConfigProperty.Builder<T> worldRestart() {
            this.restartType = RestartType.WORLD;
            return this;
        }

        @Override
        public ConfigProperty.Builder<T> gameRestart() {
            this.restartType = RestartType.GAME;
            return this;
        }

        @Override
        public ConfigProperty.Builder<T> validator(ConfigPropertyValidator<T> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public <M, B> ConfigProperty.Builder<T> metadata(ConfigMetadataType<M, B> metadataType, Consumer<B> builder) {
            this.metadata.put(metadataType, metadataType.create(builder));
            return this;
        }
    }
}
