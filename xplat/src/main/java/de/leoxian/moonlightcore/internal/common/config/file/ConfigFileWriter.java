package de.leoxian.moonlightcore.internal.common.config.file;

import de.leoxian.moonlightcore.common.config.Config;
import de.leoxian.moonlightcore.common.config.ConfigSchema;
import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class ConfigFileWriter {
    public static void writeFile(Config<?> config) {
        var filePath = config.filePath();

        List<String> serializedContent = new ArrayList<>();
        writeSchema(serializedContent, config.loadedConfig(), config.schema(), 0);

        try {
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            Files.write(filePath, serializedContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write configuration file to: " + filePath + " (" + config.id() + ")", e);
        }
    }

    private static void writeSchema(List<String> serializedContent, LoadedConfig loadedData, ConfigSchema schema, int currentDepth) {
        if (schema.comments() != null) schema.comments().forEach(line -> serializedContent.add("# " + line));

        int nextDepth = currentDepth;
        if (schema.key() != null) {
            serializedContent.add("[" + schema.key() + "]");
            serializedContent.add("");
            nextDepth = schema.key().getComponentsCount();
        }

        for (final var configValue : schema.getConfigValues()) {
            writeConfigValue(serializedContent, loadedData, configValue, nextDepth);
        }

        for (final var child : schema.getSchemas()) {
            writeSchema(serializedContent, loadedData, child, nextDepth);
        }
        serializedContent.add("");
    }

    private static <T> void writeConfigValue(List<String> serializedContent, LoadedConfig loadedData, ConfigValue<T> configValue, int currentDepth) {
        if (configValue.comments() != null) configValue.comments().forEach(line -> serializedContent.add("# " + line));

        var fullKey = configValue.key();
        List<String> relativeComponents = new ArrayList<>();
        for (int i = currentDepth; i < fullKey.getComponentsCount(); i++) {
            relativeComponents.add(fullKey.get(i));
        }
        var relativeKey = String.join(".", relativeComponents);
        T currentValue = loadedData.getRaw(configValue);
        String serializedValue = configValue.type().writeToString(currentValue);


        serializedContent.add("# Default Value: " + configValue.type().writeToString(configValue.defaultValue()));
        serializedContent.add(relativeKey + " = " + serializedValue);
        serializedContent.add("");
    }

    private ConfigFileWriter() {}
}
