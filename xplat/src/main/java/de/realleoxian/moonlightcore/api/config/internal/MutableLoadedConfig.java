package de.realleoxian.moonlightcore.api.config.internal;

import de.realleoxian.moonlightcore.api.config.schema.ConfigProperty;

public interface MutableLoadedConfig extends LoadedConfig {
    <T> void setRaw(ConfigProperty<T> property, T value);
}
