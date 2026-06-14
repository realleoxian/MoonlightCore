package de.realleoxian.moonlightcore.xplat.config.schema;

import com.google.common.base.Splitter;
import de.realleoxian.moonlightcore.api.config.ConfigKey;
import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataType;
import de.realleoxian.moonlightcore.api.config.schema.*;
import de.realleoxian.moonlightcore.xplat.config.ConfigKeyImpl;
import de.realleoxian.moonlightcore.xplat.config.ModConfigImpl;
import de.realleoxian.moonlightcore.xplat.config.schema.serializer.*;
import de.realleoxian.moonlightcore.xplat.config.schema.validator.ListConfigValueValidator;
import de.realleoxian.moonlightcore.xplat.config.schema.validator.NoOpConfigValueValidator;
import de.realleoxian.moonlightcore.xplat.config.schema.validator.RangedConfigValueValidator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigSchemaImpl implements ConfigSchema {
    private static final Splitter DOT_SPLITTER = Splitter.on('.');

    private final Map<ConfigMetadataType<?, ?>, Object> metadata;
    private final Map<String, ConfigSchema> schemas;
    private final Map<String, ConfigValue<?>> values;
    @Nullable
    private final ConfigKey configKey;  // Maybe the root schema

    public ConfigSchemaImpl(BuilderImpl builder) {
        this.metadata = Collections.unmodifiableMap(builder.metadata);
        this.values = Collections.unmodifiableMap(builder.values);
        this.configKey = builder.configKey;

        Map<String, ConfigSchema> schemas = new HashMap<>();
        for (final var builderImpl : builder.subSchemaBuilders.entrySet()) {
            schemas.put(builderImpl.getKey(), new ConfigSchemaImpl(builderImpl.getValue()));
        }
        this.schemas = Collections.unmodifiableMap(schemas);
    }

    @ApiStatus.Internal
    public void accept(ModConfigImpl config) {
        for (final var value : this.getValues()) ((ConfigValueImpl<?>) value).accept(config);
        for (final var schema : this.getSchemas()) ((ConfigSchemaImpl) schema).accept(config);
    }

    @ApiStatus.Internal
    public void invalidate() {
        for (final var value : this.getValues()) ((ConfigValueImpl<?>) value).invalidate();
        for (final var schema : this.getSchemas()) ((ConfigSchemaImpl) schema).invalidate();
    }

    @Override
    public ConfigSchema getSchema(String key) {
        if (!this.schemas.containsKey(key)) {
            throw new IllegalArgumentException("Unknown sub-schema: " + (this.configKey == null ? key : this.configKey.child(key).asFriendlyString()));
        }
        return this.schemas.get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ConfigValue<T> getValue(String key) {
        if (!this.values.containsKey(key)) {
            throw new IllegalArgumentException("Unknown config value: " + (this.configKey == null ? key : this.configKey.child(key).asFriendlyString()));
        }
        return (ConfigValue<T>) this.values.get(key);
    }

    @Override
    public @UnmodifiableView Collection<ConfigSchema> getSchemas() {
        return List.copyOf(this.schemas.values());
    }

    @Override
    public @UnmodifiableView Collection<ConfigValue<?>> getValues() {
        return List.copyOf(this.values.values());
    }

    @Override
    @Nullable
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

    public void clearListeners() {
        for (final var value : this.getValues()) ((ConfigValueImpl<?>) value).clearListeners();
        for (final var schema : this.getSchemas()) ((ConfigSchemaImpl) schema).clearListeners();
    }

    public static final class BuilderImpl implements ConfigSchema.Builder {
        private final Map<ConfigMetadataType<?, ?>, Object> metadata = new IdentityHashMap<>();
        private final Map<String, ConfigSchemaImpl.BuilderImpl> subSchemaBuilders = new HashMap<>();
        private final Map<String, ConfigValue<?>> values = new HashMap<>();

        private final Map<ConfigMetadataType<?, ?>, Object> schemaMetadata;
        @Nullable
        private final ConfigKey configKey; // Maybe the root

        public BuilderImpl(Map<ConfigMetadataType<?, ?>, Object> schemaMetadata, @Nullable ConfigKey configKey) {
            this.schemaMetadata = Map.copyOf(schemaMetadata);
            this.configKey = configKey;
        }

        @Override
        public <M, B> Builder metadata(ConfigMetadataType<M, B> type, Consumer<B> func) {
            this.metadata.put(type, type.make(func));
            return this;
        }

        @Override
        public Builder schema(String key) {
            final var builder = new BuilderImpl(this.buildMetadata(), this.buildKey(key));
            this.subSchemaBuilders.put(key, builder);
            return builder;
        }

        @Override
        public <T> ConfigValue<T> define(String key, ConfigValueSerializer<T> serializer, ConfigValueValidator<T> validator, RestartType restartType, Supplier<T> defValue) {
            final var configValue = new ConfigValueImpl<T>(this.buildMetadata(), this.buildKey(key), serializer, validator, restartType, defValue);
            this.values.put(key, configValue);
            return configValue;
        }

        @Override
        public ConfigValue<Integer> defineInt(String key, int minValue, int maxValue, RestartType restartType, Supplier<Integer> defValue) {
            return define(key, IntConfigValueSerializer.INSTANCE, new RangedConfigValueValidator<>(minValue, maxValue), restartType, defValue);
        }

        @Override
        public ConfigValue<Float> defineFloat(String key, float minValue, float maxValue, RestartType restartType, Supplier<Float> defValue) {
            return define(key, FloatConfigValueSerializer.INSTANCE, new RangedConfigValueValidator<>(minValue, maxValue), restartType, defValue);
        }

        @Override
        public ConfigValue<Boolean> defineBoolean(String key, RestartType restartType, Supplier<Boolean> defValue) {
            return define(key, BooleanConfigValueSerializer.INSTANCE, NoOpConfigValueValidator.INSTANCE.cast(), restartType, defValue);
        }

        @Override
        public <E extends Enum<E>> ConfigValue<E> defineEnum(String key, Class<E> enumClass, RestartType restartType, Supplier<E> defValue) {
            return define(key, new EnumConfigValueSerializer<>(enumClass), NoOpConfigValueValidator.INSTANCE.cast(), restartType, defValue);
        }

        @Override
        public <T> ListConfigValue<T> defineList(String key, Class<T> elementType, ConfigValueSerializer<T> elementSerializer, ConfigValueValidator<T> elementValidator, RestartType restartType, Supplier<List<T>> defValue) {
            final var configValue = new ListConfigValueImpl<T>(this.buildMetadata(), this.buildKey(key), new ListConfigValueSerializer<>(elementSerializer), new ListConfigValueValidator<>(elementValidator), restartType, defValue, elementType, elementSerializer, elementValidator);
            this.values.put(key, configValue);
            return configValue;
        }

        @Override
        public ListConfigValue<Integer> defineIntList(String key, int minValue, int maxValue, RestartType restartType, Supplier<List<Integer>> defValue) {
            return defineList(key, Integer.TYPE, IntConfigValueSerializer.INSTANCE, new RangedConfigValueValidator<>(minValue, maxValue), restartType, defValue);
        }

        @Override
        public ListConfigValue<Float> defineFloatList(String key, float minValue, float maxValue, RestartType restartType, Supplier<List<Float>> defValue) {
            return defineList(key, Float.TYPE, FloatConfigValueSerializer.INSTANCE, new RangedConfigValueValidator<>(minValue, maxValue), restartType, defValue);
        }

        @Override
        public ListConfigValue<Boolean> defineBooleanList(String key, RestartType restartType, Supplier<List<Boolean>> defValue) {
            return defineList(key, Boolean.TYPE, BooleanConfigValueSerializer.INSTANCE, NoOpConfigValueValidator.INSTANCE.cast(), restartType, defValue);
        }

        @Override
        public <E extends Enum<E>> ListConfigValue<E> defineEnumList(String key, Class<E> enumClass, RestartType restartType, Supplier<List<E>> defValue) {
            return defineList(key, enumClass, new EnumConfigValueSerializer<E>(enumClass), NoOpConfigValueValidator.INSTANCE.cast(), restartType, defValue);
        }

        private Map<ConfigMetadataType<?, ?>, Object> buildMetadata() {
            Map<ConfigMetadataType<?, ?>, Object> metadata = new HashMap<>(this.metadata);
            for (final var schemaMetadata : this.schemaMetadata.entrySet()) {
                final var metadataType = schemaMetadata.getKey();
                if (!metadataType.inherit() || metadata.containsKey(metadataType)) continue;
                metadata.put(metadataType, schemaMetadata.getValue());
            }
            this.metadata.clear();
            return metadata;
        }

        private ConfigKey buildKey(String key) {
            final var components = DOT_SPLITTER.splitToStream(key);
            return this.configKey == null ? new ConfigKeyImpl(components.toArray(String[]::new)) : this.configKey.child(key);
        }
    }
}
