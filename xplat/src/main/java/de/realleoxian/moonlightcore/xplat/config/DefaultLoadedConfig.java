package de.realleoxian.moonlightcore.xplat.config;

import de.realleoxian.moonlightcore.api.config.MutableLoadedConfig;
import de.realleoxian.moonlightcore.api.config.schema.ConfigValue;

public enum DefaultLoadedConfig implements MutableLoadedConfig {
    INSTANCE
    ;

    @Override
    public <T> void setRaw(ConfigValue<T> value, T newValue) {

    }

    @Override
    public <T> T getRaw(ConfigValue<T> value) {
        return value.getDefaultValue();
    }
}
