package de.realleoxian.moonlightcore.api.config;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValue;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface LoadedConfig {
    <T> T getRaw(ConfigValue<T> value);
}
