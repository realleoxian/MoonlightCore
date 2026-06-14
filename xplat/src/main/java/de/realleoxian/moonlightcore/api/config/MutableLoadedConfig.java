package de.realleoxian.moonlightcore.api.config;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValue;

public interface MutableLoadedConfig extends LoadedConfig {
    <T> void setRaw(ConfigValue<T> value, T newValue);
}
