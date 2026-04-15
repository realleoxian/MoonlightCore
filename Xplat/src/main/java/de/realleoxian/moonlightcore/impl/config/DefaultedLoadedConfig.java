package de.realleoxian.moonlightcore.impl.config;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigProperty;
import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyType;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSchema;
import de.realleoxian.moonlightcore.api.config.schema.ConfigSection;
import de.realleoxian.moonlightcore.api.misc.PathUtils;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum DefaultedLoadedConfig implements LoadedConfig {
    INSTANCE
    ;

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

    @ApiStatus.Internal
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

    @ApiStatus.Internal
    private <T> void saveProperty(List<String> serialized, ConfigProperty<T> property) {
        if (property.getComments() != null)
            property.getComments().forEach(comment -> serialized.add("# " + comment));

        ConfigPropertyType<T> type = property.getType();
        T defaultValue = property.getDefault().get();
        serialized.add("# Default Value: " + type.write(defaultValue));
        serialized.add("%s = %s".formatted(property.getKey().getLastComponent(), type.write(defaultValue)));
        serialized.add("");
    }

    @Override
    public <T> void setRaw(ConfigProperty<T> property, T value) {
        // no-op
    }

    @Override
    public <T> T getRaw(ConfigProperty<T> property) {
        return property.getDefault().get();
    }
}
