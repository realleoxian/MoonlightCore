package de.realleoxian.moonlightcore.impl.config;

import de.realleoxian.moonlightcore.api.config.ModConfig;
import de.realleoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigProperty;

public enum DefaultedLoadedConfig implements LoadedConfig {
    INSTANCE
    ;

    @Override
    public void save(ModConfig config) {
        // no-op
    }

    @Override
    public <T> void setRaw(ConfigProperty<T> property, T value) {
        // no-op
    }

    @Override
    public <T> T getRaw(ConfigProperty<T> property) {
        return property.get();
    }
}
