package de.realleoxian.moonlightcore.xplat.config.internal;

import de.realleoxian.moonlightcore.api.config.internal.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigKey;
import de.realleoxian.moonlightcore.api.config.schema.ConfigProperty;
import de.realleoxian.moonlightcore.xplat.config.schema.ConfigKeyImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class LoadedConfigImpl implements MutableLoadedConfig {
    private static final Pattern SCHEMA_REGEX = Pattern.compile("\\s*\\[(?<category>.*)]$");
    private static final Pattern KEY_REGEX = Pattern.compile("\\s*(?<key>.*)\\s*=\\s*(?<value>.*)\\s*$");
    private static final Pattern COMMENT_REGEX = Pattern.compile("^#.*");

    private final Map<ConfigKey, String> values = new HashMap<>();

    public LoadedConfigImpl(List<String> content) {
        ConfigKey currentCategory = null;
        for (final String line : content) {
            if (line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) {
                continue;
            }

            var propertyMatcher = KEY_REGEX.matcher(line);
            if (propertyMatcher.matches()) {
                var rawKey = propertyMatcher.group("key");
                var key = currentCategory == null ? new ConfigKeyImpl(rawKey) : currentCategory.child(rawKey);
                this.values.put(key, propertyMatcher.group("value"));
                continue;
            }

            var schemaMatcher = SCHEMA_REGEX.matcher("category");
            if (schemaMatcher.matches()) {
                currentCategory = new ConfigKeyImpl(schemaMatcher.group("category"));
            }
        }
    }

    public LoadedConfigImpl(Path filePath) throws IOException {
        this (Files.readAllLines(filePath));
    }

    @Override
    public <T> void setRaw(ConfigProperty<T> property, T value) {
        this.values.put(property.key(), property.type().toString(value));
    }

    @Override
    public <T> T getRaw(ConfigProperty<T> property) {
        if (!this.values.containsKey(property.key())) {
            return property.defaultValue().get();
        }

        return property.type().fromString(this.values.get(property.key()));
    }
}
