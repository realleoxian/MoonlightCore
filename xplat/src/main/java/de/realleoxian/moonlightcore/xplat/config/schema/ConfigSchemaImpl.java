package de.realleoxian.moonlightcore.xplat.config.schema;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataType;
import de.realleoxian.moonlightcore.api.config.schema.*;
import de.realleoxian.moonlightcore.api.config.schema.validator.ConfigPropertyValidator;
import de.realleoxian.moonlightcore.xplat.config.ModConfigImpl;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ConfigSchemaImpl implements ConfigSchema {
    private final ConfigKey key;
    private final Map<String, ConfigSchema> schemas;
    private final Map<String, ConfigProperty<?>> properties;
    private final Map<ConfigMetadataType<?, ?>, Object> metadata;

    public ConfigSchemaImpl(Builder builder) {
        this.key = builder.key;
        this.schemas = Map.copyOf(builder.schemas);
        this.properties = Map.copyOf(builder.properties);
        this.metadata = Map.copyOf(builder.metadata);
    }

    @ApiStatus.Internal
    public void validate(LoadedConfig loadedConfig, ModConfigImpl config) {
        for (ConfigProperty<?> property : this.properties()) {
            ((ConfigPropertyImpl<?>) property).validate(loadedConfig, config);
        }

        for (ConfigSchema schema : this.schemas()) {
            ((ConfigSchemaImpl) schema).validate(loadedConfig, config);
        }
    }

    @Override
    public ConfigProperty<?> getProperty(String key) {
        return null;
    }

    @Override
    public ConfigSchema getSchema(String key) {
        return null;
    }

    @Override
    public @UnmodifiableView Collection<ConfigProperty<?>> properties() {
        return Collections.unmodifiableCollection(this.properties.values());
    }

    @Override
    public @UnmodifiableView Collection<ConfigSchema> schemas() {
        return Collections.unmodifiableCollection(this.schemas.values());
    }

    @Override
    public ConfigKey key() {
        return this.key;
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

    public static final class Builder implements ConfigSchema.Builder {
        private final Map<String, ConfigSchema> schemas = new HashMap<>();
        private final Map<String, ConfigProperty<?>> properties = new HashMap<>();
        private final Map<ConfigMetadataType<?, ?>, Object> metadata = new HashMap<>();

        private final ConfigKey key;

        public Builder(ConfigKey key) {
            this.key = key;
        }

        @Override
        public ConfigSchema.Builder schema(String key, Consumer<ConfigSchema.Builder> builderModifier) {
            final var builder = new Builder(this.key.child(key));
            builderModifier.accept(builder);
            this.schemas.put(key, new ConfigSchemaImpl(builder));
            this.metadata.clear();

            return this;
        }

        @Override
        public <T> ConfigProperty<T> property(String key, ConfigPropertyType<T> type, Supplier<T> defaultValue, Consumer<ConfigProperty.Builder<T>> builderModifier) {
            final var builder = new ConfigPropertyImpl.Builder<T>(this.key.child(key), type, defaultValue);
            builderModifier.accept(builder);
            final var property = new ConfigPropertyImpl<T>(builder);
            this.properties.put(key, property);
            this.metadata.clear();
            return property;
        }

        @Override
        public <T> ListConfigProperty<T> listProperty(String key, ConfigPropertyType<T> elementType, ConfigPropertyValidator<T> elementValidator, Supplier<List<T>> defaultValue, Consumer<ConfigProperty.Builder<List<T>>> builderModifier) {
            final var builder = new ConfigPropertyImpl.Builder<>(this.key.child(key), ConfigPropertyType.listType(elementType), defaultValue);
            builderModifier.accept(builder);
            final var property = new ListConfigPropertyImpl<>(builder, elementValidator, elementType);
            this.properties.put(key, property);
            this.metadata.clear();
            return property;
        }

        @Override
        public <M, B> ConfigSchema.Builder metadata(ConfigMetadataType<M, B> metadataType, Consumer<B> builder) {
            this.metadata.put(metadataType, metadataType.create(builder));
            return this;
        }
    }
}
