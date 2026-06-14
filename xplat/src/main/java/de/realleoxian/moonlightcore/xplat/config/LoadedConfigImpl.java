package de.realleoxian.moonlightcore.xplat.config;

import com.google.common.base.Splitter;
import de.realleoxian.moonlightcore.api.config.ConfigKey;
import de.realleoxian.moonlightcore.api.config.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class LoadedConfigImpl implements MutableLoadedConfig {
    private static final Splitter DOT_SPLITTER = Splitter.on('.');
    private static final Pattern COMMENT_REGEX = Pattern.compile("#.*");
    private static final Pattern SCHEMA_REGEX = Pattern.compile("^\\[(?<key>.*)]");
    private static final Pattern VALUE_REGEX = Pattern.compile("^\\s*(?<key>.*)\\s*=\\s*(?<value>.*)");

    private final Map<ConfigKey, String> loadedValues = new HashMap<>();

    public LoadedConfigImpl(List<String> config) {
        if (!config.isEmpty()) {
            final var it = config.iterator();

            var currentSchema = (ConfigKey) null;
            while (it.hasNext()) {
                final var line = it.next();

                if (line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) {
                    continue;
                }

                final var valueMatcher = VALUE_REGEX.matcher(line);
                if (valueMatcher.matches()) {
                    final var rawKey = valueMatcher.group("key");
                    final var rawValue = valueMatcher.group("value");

                    final var key = currentSchema == null ? new ConfigKeyImpl(DOT_SPLITTER.splitToList(rawKey).toArray(String[]::new)) : currentSchema.child(rawKey);
                    this.loadedValues.put(key, rawValue);
                    continue;
                }

                final var schemaRegex = SCHEMA_REGEX.matcher(line);
                if (schemaRegex.matches()) {
                    final var key = schemaRegex.group("key");
                    currentSchema = new ConfigKeyImpl(DOT_SPLITTER.splitToList(key).toArray(String[]::new));
                }
            }
        }
    }

    public LoadedConfigImpl(Path filePath) throws IOException {
        this (Files.readAllLines(filePath));
    }

    @Override
    public <T> void setRaw(ConfigValue<T> value, T newValue) {
        this.loadedValues.put(value.getKey(), value.getSerializer().writeToString(newValue));
    }

    @Override
    public <T> T getRaw(ConfigValue<T> value) {
        final var key = value.getKey();
        if (!loadedValues.containsKey(key)) return value.getDefaultValue();

        final var result = value.getSerializer().readFromString(loadedValues.get(key));
        if (!result.isSuccess()) return value.getDefaultValue();
        return result.asSuccess().value();
    }
}
