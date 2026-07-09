package de.leoxian.moonlightcore.internal.common.config;

import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.common.config.schema.RestartType;
import de.leoxian.moonlightcore.common.config.schema.type.ConfigValueType;
import de.leoxian.moonlightcore.common.config.schema.validator.ConfigValueValidator;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public final class ConfigValueImpl<T> implements ConfigValue<T> {
    private final ConfigKey key;
    private final ConfigValueType<T> type;
    private final ConfigValueValidator<T> validator;
    private final RestartType requiredRestartType;
    private final Supplier<T> defaultValue;
    private final @Nullable String translationKey;
    private final @Nullable Iterable<String> comments;

    private ConfigImpl<?> owner = null;
    private volatile T cachedValue = null;

    ConfigValueImpl(ConfigKey key, ConfigValueType<T> type, ConfigValueValidator<T> validator, RestartType requiredRestartType, Supplier<T> defaultValue, @Nullable String translationKey, @Nullable Iterable<String> comments) {
        this.key = key;
        this.type = type;
        this.validator = validator;
        this.requiredRestartType = requiredRestartType;
        this.defaultValue = defaultValue;
        this.translationKey = translationKey;
        this.comments = comments;
    }

    @Override
    public T get() {
        T result = this.cachedValue;
        if (result == null) {
            this.owner.lock.lock();
            try {
                result = this.cachedValue;
                if (result == null) {
                    this.cachedValue = result = getRaw();
                }
            } finally {
                this.owner.lock.unlock();
            }
        }
        return result;
    }

    @Override
    public T defaultValue() {
        return this.defaultValue.get();
    }

    @Override
    public ConfigValueType<T> type() {
        return this.type;
    }

    @Override
    public ConfigValueValidator<T> validator() {
        return this.validator;
    }

    @Override
    public RestartType requiredRestartType() {
        return this.requiredRestartType;
    }

    @Override
    public ConfigKey key() {
        return this.key;
    }

    @Override
    public @Nullable Iterable<String> comments() {
        return this.comments;
    }

    @Override
    public @Nullable String translationKey() {
        return this.translationKey;
    }

    void setup(ConfigImpl<?> owner) {
        this.owner = Objects.requireNonNull(owner, "Config owner may not be 'null'");
    }

    void invalidate() {
        if (this.owner == null) {
            throw new IllegalStateException("Cannot invalidate an config value that doesn't have a parent");
        }
        this.cachedValue = null;
    }

    T getRaw() {
        this.owner.lock.lock();
        try {
            return this.owner.loadedData.getRaw(this);
        } finally {
            this.owner.lock.unlock();
        }
    }
}
