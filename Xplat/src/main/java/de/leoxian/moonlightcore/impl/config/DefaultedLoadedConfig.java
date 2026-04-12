package de.leoxian.moonlightcore.impl.config;

import de.leoxian.moonlightcore.api.config.ModConfig;
import de.leoxian.moonlightcore.api.config.internal.LoadedConfig;
import de.leoxian.moonlightcore.api.config.schema.ConfigProperty;

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
