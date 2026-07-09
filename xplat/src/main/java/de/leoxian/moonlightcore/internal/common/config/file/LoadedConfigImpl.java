package de.leoxian.moonlightcore.internal.common.config.file;

import de.leoxian.moonlightcore.common.config.Config;
import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.common.config.schema.ConfigKey;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

public final class LoadedConfigImpl implements LoadedConfig {
    private static final Pattern COMMENT_REGEX = Pattern.compile("#.*");
    private static final Pattern SCHEMA_REGEX = Pattern.compile("\\[(?<key>.*)]$");
    private static final Pattern VALUE_REGEX = Pattern.compile("\\s*(?<key>.*)\\s*=\\s*(?<value>.*)\\s*$");

    public static Optional<LoadedConfig> load(Config<?> config) throws IOException {
        if (!Files.exists(config.filePath())) return Optional.empty();
        List<String> content = Files.readAllLines(config.filePath());

        Map<ConfigKey, Object> loadedData = new HashMap<>();
        var currentSchema = config.schema();
        for (final var line : content) {
            if (line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) continue;

            var valueMatcher = VALUE_REGEX.matcher(line);
            if (valueMatcher.matches()) {
                var key = new ConfigKey(valueMatcher.group("key"));
                var targetSchema = currentSchema;
                for (int i = 0; i < key.getComponentsCount() - 1; i++) {
                    targetSchema = targetSchema.getSection(key.get(i));
                    if (targetSchema == null) {
                        throw new IllegalStateException("Expected a valid config schema path, instead got invalid schema at index " + i + " on path " + key);
                    }
                }

                var configValue = targetSchema.getValue(key.lastComponent());
                if (configValue == null) {
                    throw new IllegalStateException("Expected a valid config value, got: " + key.lastComponent());
                }

                configValue.type().readFromString(valueMatcher.group("value"))
                        .ifSuccess((t) -> loadedData.put(configValue.key(), t))
                        .ifError(errors -> {
                            RuntimeException errorsException = new RuntimeException("Found unexpected errors parsing config value: " + configValue.key());
                            for (final var error : errors) errorsException.addSuppressed(new RuntimeException(error));
                            errorsException.printStackTrace();
                            // Fallback to the default value
                            loadedData.put(configValue.key(), configValue.defaultValue());
                        });
            }

            var schemaMatcher = SCHEMA_REGEX.matcher(line);
            if (schemaMatcher.matches()) {
                var key = new ConfigKey(schemaMatcher.group("key"));
                var targetSchema = config.schema();

                for (int i = 0; i < key.getComponentsCount(); i++) {
                    targetSchema = targetSchema.getSection(key.get(i));
                    if (targetSchema == null) {
                        throw new IllegalStateException("Expected a valid config schema path, instead got invalid schema at index " + i + " on path " + key);
                    }
                }
                currentSchema = targetSchema;
            }
        }
        return Optional.of(new LoadedConfigImpl(loadedData));
    }

    private final Map<ConfigKey, Object> data;

    public LoadedConfigImpl(Map<ConfigKey, Object> data) {
        this.data = data;
    }

    public LoadedConfigImpl() {
        this.data = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getRaw(ConfigValue<T> configValue) {
        var ret = this.data.get(configValue.key());
        return ret == null ? configValue.defaultValue() : (T) ret;
    }

    @Override
    public <T> void setRaw(ConfigValue<T> configValue, T newValue) {
        this.data.put(configValue.key(), newValue);
    }
}
