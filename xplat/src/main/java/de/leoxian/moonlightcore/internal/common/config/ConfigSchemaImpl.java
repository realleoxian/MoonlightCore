package de.leoxian.moonlightcore.internal.common.config;

import de.leoxian.moonlightcore.common.config.ConfigSchema;
import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.common.config.schema.RestartType;
import de.leoxian.moonlightcore.common.config.schema.type.ConfigValueType;
import de.leoxian.moonlightcore.common.config.schema.validator.ConfigValueValidator;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public final class ConfigSchemaImpl implements ConfigSchema {
    private final ConfigKey key;

    private final Map<String, ConfigSchemaImpl> children = new LinkedHashMap<>();
    private final List<ConfigValueImpl<?>> configValues = new ArrayList<>();
    private final List<String> comments = new ArrayList<>();
    private String translationKey = null;

    ConfigSchemaImpl(ConfigKey key) {
        this.key = key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> ConfigValue<T> getValue(String key) {
        for (final var configValue : configValues) {
            if (configValue.key().lastComponent().equals(key)) {
                return (ConfigValue<T>) configValue;
            }
        }
        return null;
    }

    @Override
    public @Nullable ConfigSchemaImpl getSection(String key) {
        return this.children.get(key);
    }

    @Override
    public Collection<ConfigValue<?>> getConfigValues() {
        return List.copyOf(this.configValues);
    }

    @Override
    public Collection<ConfigSchema> getSchemas() {
        return Collections.unmodifiableCollection(this.children.values());
    }

    @Override
    public @Nullable ConfigKey key() {
        return this.key;
    }

    @Override
    public Iterable<String> comments() {
        return this.comments;
    }

    @Override
    public @Nullable String translationKey() {
        return this.translationKey;
    }

    void setup(ConfigImpl<?> owner) {
        for (final var configValue : this.configValues) configValue.setup(owner);
        for (final var child : this.children.values()) child.setup(owner);
    }

    void invalidate() {
        for (final var configValue : this.configValues) configValue.invalidate();
        for (final var child : this.children.values()) child.invalidate();
    }

    public static final class BuilderImpl implements Builder {
        private final List<String> pathStack = new ArrayList<>();
        private final List<ConfigSchemaImpl> schemaStack = new ArrayList<>();

        private final List<String> currentComments = new ArrayList<>();
        private RestartType requiredRestartType = RestartType.NONE;
        private String currentTranslationKey = null;

        BuilderImpl(ConfigSchemaImpl rootSchema) {
            this.schemaStack.add(rootSchema);
        }

        @Override
        public Builder push(String key) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("Section namespace key cannot be null or empty");
            }

            this.pathStack.add(key);

            var absoluteKey = new ConfigKey(this.pathStack.toArray(String[]::new));
            var activeParent = schemaStack.getLast();
            var childSchema = activeParent.getSection(key);
            if (childSchema == null) {
                childSchema = new ConfigSchemaImpl(absoluteKey);
                if (!currentComments.isEmpty()) {
                    childSchema.comments.addAll(this.currentComments);
                    this.currentComments.clear();
                }
                if (currentTranslationKey != null) {
                    childSchema.translationKey = currentTranslationKey;
                    this.currentTranslationKey = null;
                }
                activeParent.children.put(key, childSchema);
            }
            schemaStack.add(childSchema);
            return this;
        }

        @Override
        public Builder pop() {
            if (pathStack.isEmpty()) {
                throw new IllegalStateException("Mismatched block balancing: cannot pop beyond root schema level.");
            }
            this.pathStack.removeLast();
            this.schemaStack.removeLast();
            return this;
        }

        @Override
        public Builder translationKey(String translationKey) {
            this.currentTranslationKey = translationKey;
            return this;
        }

        @Override
        public Builder requiresWorldRestart() {
            this.requiredRestartType = RestartType.WORLD;
            return this;
        }

        @Override
        public Builder requiresGameRestart() {
            this.requiredRestartType = RestartType.GAME;
            return this;
        }

        @Override
        public Builder comment(String comment) {
            for (final var line : comment.split("\n")) {
                this.currentComments.addLast(line);
            }
            return this;
        }

        @Override
        public <T> ConfigValue<T> define(String key, ConfigValueType<T> type, ConfigValueValidator<T> validator, Supplier<T> defValue) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException("Config value property key cannot be null or empty");
            }

            List<String> fullPath = new ArrayList<>(this.pathStack);
            fullPath.addAll(Arrays.asList(key.split("\\.")));
            var absoluteKey = new ConfigKey(fullPath.toArray(String[]::new));
            var configValue = new ConfigValueImpl<T>(
                    absoluteKey,
                    type,
                    validator,
                    this.requiredRestartType,
                    defValue,
                    this.currentTranslationKey,
                    List.copyOf(this.currentComments)
            );
            var activeSchema = schemaStack.getLast();
            activeSchema.configValues.add(configValue);

            this.currentComments.clear();
            this.currentTranslationKey = null;
            this.requiredRestartType = RestartType.NONE;
            return configValue;
        }
    }
}
