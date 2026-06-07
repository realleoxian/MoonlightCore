package de.realleoxian.moonlightcore.xplat.config.internal;

import de.realleoxian.moonlightcore.api.config.internal.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigProperty;

public enum DefaultLoadedConfig implements MutableLoadedConfig {
    INSTANCE
    ;

    @Override
    public <T> void setRaw(ConfigProperty<T> property, T value) {

    }

    @Override
    public <T> T getRaw(ConfigProperty<T> property) {
        return property.defaultValue().get();
    }
}
