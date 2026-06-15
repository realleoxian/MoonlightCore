package de.realleoxian.moonlightcore.xplat.config.schema;

import de.realleoxian.moonlightcore.api.config.ConfigKey;
import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataType;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValue;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValueSerializer;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValueValidator;
import de.realleoxian.moonlightcore.api.config.schema.RestartType;
import de.realleoxian.moonlightcore.xplat.config.ModConfigImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ConfigValueImpl<T> implements ConfigValue<T> {
    private final Map<ConfigMetadataType<?, ?>, Object> metadata;
    private final ConfigKey configKey;
    private final ConfigValueSerializer<T> serializer;
    private final ConfigValueValidator<T> validator;
    private final RestartType restartType;
    private final Supplier<T> defaultValue;

    private List<ChangeListener<T>> listeners = null;
    private ModConfigImpl config = null;
    private T cachedValue;

    ConfigValueImpl(Map<ConfigMetadataType<?, ?>, Object> metadataType, ConfigKey configKey, ConfigValueSerializer<T> serializer, ConfigValueValidator<T> validator, RestartType restartType, Supplier<T> defaultValue) {
        this.metadata = metadataType;
        this.configKey = configKey;
        this.serializer = serializer;
        this.validator = validator;
        this.restartType = restartType;
        this.defaultValue = defaultValue;
    }

    @Override
    public void set(T value, boolean shouldSave) {
        this.config.lock.lock();
        try {
            if (Objects.equals(this.cachedValue, value)) {
                return;
            }

            if (this.validator.test(value)) {
                if (this.restartType == RestartType.NONE) {
                    final T snapshot = this.cachedValue;
                    this.cachedValue = value;
                    if (this.listeners != null) {
                        this.listeners.forEach(l -> l.onConfigValueChange(snapshot, this.cachedValue));
                    }
                }
                this.config.loadedConfig.setRaw(this, value);
                this.config.markKeyDirty(this.configKey);
                if (shouldSave) {
                    this.config.markDirty();
                }
            }
        } finally {
            this.config.lock.unlock();
        }
    }

    @ApiStatus.Internal
    public void accept(ModConfigImpl config) {
        this.config = config;
    }

    @ApiStatus.Internal
    public void invalidate() {
        this.config.lock.lock();
        try {
            this.cachedValue = null;
        } finally {
            this.config.lock.unlock();
        }
    }

    @ApiStatus.Internal
    public void clearListeners() {
        if (this.listeners != null) {
            this.listeners = null;
        }
    }

    @Override
    public void registerListener(ChangeListener<T> listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>(4);
        }
        this.listeners.add(listener);
    }

    @Override
    public T getValue() {
        T value = this.cachedValue;
        if (value == null) {
            this.config.lock.lock();
            try {
                value = this.cachedValue;
                if (value == null) {
                    value = this.config.loadedConfig.getRaw(this);
                    this.cachedValue = value;
                }
            } finally {
                this.config.lock.unlock();
            }
        }
        return value;
    }

    @Override
    public T getDefaultValue() {
        return this.defaultValue.get();
    }

    @Override
    public ConfigValueSerializer<T> getSerializer() {
        return this.serializer;
    }

    @Override
    public ConfigValueValidator<T> getValidator() {
        return this.validator;
    }

    @Override
    public boolean requiresWorldRestart() {
        return this.restartType == RestartType.WORLD;
    }

    @Override
    public boolean requiresGameRestart() {
        return this.restartType == RestartType.GAME;
    }

    @Override
    public ConfigKey getKey() {
        return this.configKey;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <M> M getMetadata(ConfigMetadataType<M, ?> type) {
        return (M) this.metadata.get(type);
    }

    @Override
    public <M> boolean hasMetadata(ConfigMetadataType<M, ?> type) {
        return this.metadata.containsKey(type);
    }

    @Override
    public @UnmodifiableView Map<ConfigMetadataType<?, ?>, ?> getMetadata() {
        return Map.copyOf(this.metadata);
    }
}
