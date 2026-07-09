package de.leoxian.moonlightcore.internal.common.config.file;

import de.leoxian.moonlightcore.common.config.ConfigValue;
import de.leoxian.moonlightcore.common.config.file.LoadedConfig;

public enum DefaultLoadedConfig implements LoadedConfig {
    INSTANCE
    ;

    @Override
    public <T> T getRaw(ConfigValue<T> configValue) {
        return configValue.defaultValue();
    }

    @Override
    public <T> void setRaw(ConfigValue<T> configValue, T newValue) {
        // no-op
    }
}
