package de.leoxian.moonlightcore.internal.common.config.file;

import de.leoxian.moonlightcore.common.config.Config;
import de.leoxian.moonlightcore.common.config.ConfigSchema;
import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConfigParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigParser.class);
    private static final Map<Path, FileTime> RECENTLY_MODIFIED_FILES = Collections.synchronizedMap(new HashMap<>());
    private static final Pattern COMMENT_REGEX = Pattern.compile("#.*");
    private static final Pattern SCHEMA_REGEX = Pattern.compile("\\[(?<key>.*)]$");
    private static final Pattern VALUE_REGEX = Pattern.compile("\\s*(?<key>.*)\\s*=\\s*(?<value>.*)\\s*$");

    public static Optional<LoadedConfig> parse(Config<?> config) throws IOException {
        Path filePath = config.filePath();
        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        FileTime lastModifiedTime = Files.getLastModifiedTime(filePath);
        FileTime lastKnownModifiedTime = RECENTLY_MODIFIED_FILES.get(filePath);

        if (lastKnownModifiedTime != null && lastKnownModifiedTime.compareTo(lastModifiedTime) >= 0) {
            LOGGER.info("Config file '{}' was recently loaded and no changes have occurred since last load", filePath);
            return Optional.of(config.loadedConfig());
        }

        Map<ConfigKey, Object> loadedData = new HashMap<>();
        ConfigSchema rootSchema = config.schema();
        ConfigSchema currentSchema = rootSchema;

        for (String line : Files.readAllLines(filePath)) {
            if (line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) {
                continue;
            }

            Matcher valueMatcher = VALUE_REGEX.matcher(line);
            if (valueMatcher.matches()) {
                ConfigKey key = new ConfigKey(valueMatcher.group("key"));
                ConfigValue<?> configValue = findConfigValue(currentSchema, rootSchema, key);
                if (configValue == null) {
                    LOGGER.warn("Invalid config value entry was found on '{}'. Skipping", filePath);
                    continue;
                }

                configValue.type().readFromString(valueMatcher.group("value"))
                        .ifSuccess(parsed -> loadedData.put(configValue.key(), parsed))
                        .ifError(errors -> {
                            RuntimeException ex = new RuntimeException();
                            for (final String error : errors) {
                                ex.addSuppressed(new RuntimeException(error));
                            }
                            LOGGER.warn("Invalid values where found parsing config value '{}'. Using default value", configValue.key(), ex);
                            loadedData.put(configValue.key(), configValue.defaultValue());
                        });
            } else {
                Matcher schemaMatcher = SCHEMA_REGEX.matcher(line);
                if (schemaMatcher.matches()) {
                    ConfigKey key = new ConfigKey(schemaMatcher.group("key"));
                    ConfigSchema schema = findSchema(currentSchema, rootSchema, key);
                    if (schema == null) {
                        LOGGER.warn("Invalid config section was found on '{}'. Skipping", filePath);
                        continue;
                    }
                    currentSchema = schema;
                }
            }
        }

        RECENTLY_MODIFIED_FILES.put(filePath, lastModifiedTime);
        return Optional.of(new LoadedConfigImpl(loadedData));
    }


    private static ConfigSchema findSchema(ConfigSchema schema, ConfigKey key, int length) {
        ConfigSchema target = schema;
        for (int i = 0; i < length; i++) {
            target = target.getSection(key.get(i));
            if (target == null) {
                return null;
            }
        }
        return target;
    }

    private static ConfigSchema findSchema(ConfigSchema current, ConfigSchema root, ConfigKey key) {
        ConfigSchema target = current;
        for (int i = 0; i < key.getComponentsCount(); i++) {
            target = target.getSection(key.get(i));
            if (target == null) {
                break;
            }
        }

        if (target != null) {
            return target;
        }

        target = root;
        for (int i = 0; i < key.getComponentsCount(); i++) {
            target = target.getSection(key.get(i));
            if (target == null) {
                break;
            }
        }
        return target;
    }

    private static ConfigValue<?> findConfigValue(ConfigSchema schema, ConfigSchema rootSchema, ConfigKey key) {
        int count = key.getComponentsCount();
        if (count == 1) {
            ConfigValue<?> value = schema.getValue(key.lastComponent());
            return value != null ? value : rootSchema.getValue(key.lastComponent());
        }

        ConfigSchema target = findSchema(schema, key, count - 1);
        if (target != null) {
            ConfigValue<?> value = target.getValue(key.lastComponent());
            if (value != null) {
                return value;
            }
        }

        target = findSchema(rootSchema, key, count - 1);
        return target != null ? target.getValue(key.lastComponent()) : null;
    }

    private ConfigParser() {
    }
}
