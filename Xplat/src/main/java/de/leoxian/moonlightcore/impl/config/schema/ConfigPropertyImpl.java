package de.leoxian.moonlightcore.impl.config.schema;

import de.leoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.leoxian.moonlightcore.api.config.schema.*;
import de.leoxian.moonlightcore.impl.config.ModConfigImpl;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;

public class ConfigPropertyImpl<T> implements ConfigProperty<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger("moonlightcore:config-api");

    private final ConfigKey key;
    private final ConfigPropertyType<T> type;
    private final ConfigPropertyValidator<T> validator;
    private final RestartType restartType;
    private final @Nullable Iterable<String> comments;
    private final @Nullable String translationKey;

    private final Supplier<T> defaultValue;
    private volatile T cached = null;

    private ModConfigImpl parent;
    private LoadedConfig loadedConfig;

    ConfigPropertyImpl(ConfigKey key, ConfigPropertyType<T> type, ConfigPropertyValidator<T> validator, RestartType restartType, @Nullable Iterable<String> comments, @Nullable String translationKey, Supplier<T> defaultValue) {
        this.key = key;
        this.type = type;
        this.validator = validator;
        this.restartType = restartType;
        this.comments = comments;
        this.translationKey = translationKey;
        this.defaultValue = defaultValue;
    }

    @ApiStatus.Internal
    public void setValue(T value) {
        Objects.requireNonNull(this.parent, "ModConfig parent cannot be 'null' at value set");
        Objects.requireNonNull(this.loadedConfig, "Loaded config cannot be 'null' at value set");

        if (!this.validator.test(value)) {
            LOGGER.error("Tried to ser invalid value : {}\n{}", value, this.validator.getValidValueDescription().getString());
        } else {
            if (value != this.cached) {
                this.loadedConfig.setRaw(this, value);
                this.parent.markDirty();

                if (restartType == RestartType.NONE)
                    this.cached = value;
            }
        }
    }

    @ApiStatus.Internal
    public void setup(ConfigSchemaImpl schema) {
        Objects.requireNonNull(schema, "Config Schema cannot be 'null'");
        Objects.requireNonNull(schema.parent, "ModConfig parent cannot be 'null' at setup");
        Objects.requireNonNull(schema.config, "Laded config cannot be 'null' at setup");

        this.parent = schema.parent;
        this.loadedConfig = schema.config;
    }

    @Override
    public T get() {
        T ret = this.cached;
        if (ret == null) {
            Objects.requireNonNull(this.parent, "Cannot retrieve the current loaded value of property '" + this.key + "' with a 'null' mod config parent");

            this.parent.lock.lock();
            ret = this.cached = this.loadedConfig.getRaw(this);
            if (ret == null) throw new IllegalArgumentException("Couldn't find any valid value for property '" + this.key + "'");
            this.parent.lock.unlock();
        }

        return ret;
    }

    @Override
    public Supplier<T> getDefault() {
        return this.defaultValue;
    }

    @Override
    public ConfigPropertyType<T> getType() {
        return this.type;
    }

    @Override
    public ConfigPropertyValidator<T> getValidator() {
        return this.validator;
    }

    @Override
    public @Nullable Iterable<String> getComments() {
        return this.comments;
    }

    @Override
    public @Nullable String getTranslationKey() {
        return this.translationKey;
    }

    @Override
    public RestartType getRestartType() {
        return this.restartType;
    }

    @Override
    public ConfigKey getKey() {
        return this.key;
    }
}
