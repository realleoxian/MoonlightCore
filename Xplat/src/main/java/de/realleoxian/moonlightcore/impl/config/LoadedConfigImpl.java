package de.realleoxian.moonlightcore.impl.config;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.*;
import de.realleoxian.moonlightcore.impl.config.schema.ConfigKeyImpl;
import de.realleoxian.moonlightcore.api.misc.PathUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadedConfigImpl implements LoadedConfig {
    private static final Pattern COMMENT_REGEX = Pattern.compile("#.*");
    private static final Pattern SECTION_REGEX = Pattern.compile("\\[(?<key>.*)]$");
    private static final Pattern PROPERTY_REGEX = Pattern.compile("\\s*(?<key>.*)\\s*=\\s*(?<value>.*)\\s*$");

    public static LoadedConfig fromBytes(byte[] bytes) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8))){
            String line;
            while ((line = reader.readLine()) != null)
                lines.add(line);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config from bytes", e);
        }

        return new LoadedConfigImpl(lines);
    }

    private final Map<ConfigKey, String> values = new HashMap<>();

    public LoadedConfigImpl(List<String> lines) {
        ConfigKey currentSection = null;

        for (String line : lines) {
            if (line.isEmpty() || COMMENT_REGEX.matcher(line).matches()) {
                continue;
            }

            Matcher sectionMatcher = SECTION_REGEX.matcher(line);
            if (sectionMatcher.matches()) {
                currentSection = new ConfigKeyImpl(sectionMatcher.group("key"));
                continue;
            }

            Matcher propertyMatcher = PROPERTY_REGEX.matcher(line);
            if (propertyMatcher.matches()) {
                String key = propertyMatcher.group("key");
                ConfigKey fullKey = currentSection == null ? new ConfigKeyImpl(key) : currentSection.child(key);
                String value = propertyMatcher.group("value");

                if (this.values.putIfAbsent(fullKey, value) != null) {
                    throw new IllegalArgumentException("Duplicated config property definition: " + fullKey);
                }
            }
        }
    }

    @Override
    public void save(ModConfig config) {
        ConfigSchema schema = config.getSchema();

        List<String> serialized = new ArrayList<>();
        for (ConfigProperty<?> property : schema.getRootProperties())
            saveProperty(serialized, property);
        for (ConfigSection section : schema.getSections())
            saveSection(serialized, section);

        try {
            PathUtils.writeUsingTempFile(config.getFilePath(), serialized);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveSection(List<String> serialized, ConfigSection section) {
        if (section.getComments() != null)
            section.getComments().forEach(comment -> serialized.add("# " + comment));

        serialized.add("[%s]".formatted(section.getKey()));
        for (ConfigProperty<?> property : section.getProperties())
            saveProperty(serialized, property);
        for (ConfigSection subSection : section.getSubSections())
            saveSection(serialized, subSection);
        serialized.add("");
    }

    private <T> void saveProperty(List<String> serialized, ConfigProperty<T> property) {
        if (property.getComments() != null)
            property.getComments().forEach(comment -> serialized.add("# " + comment));

        ConfigPropertyType<T> type = property.getType();
        serialized.add("# " + type.write(property.getDefault().get()));
        serialized.add("%s = %s".formatted(property.getKey().getLastComponent(), type.write(getRaw(property))));
        serialized.add("");
    }

    @Override
    public <T> void setRaw(ConfigProperty<T> property, T value) {
        this.values.put(property.getKey(), property.getType().write(value));
    }

    @Override
    public <T> T getRaw(ConfigProperty<T> property) {
        ConfigKey key = property.getKey();
        ConfigPropertyType<T> type = property.getType();

        return this.values.containsKey(key) ? type.read(this.values.get(key)) : property.getDefault().get();
    }
}
