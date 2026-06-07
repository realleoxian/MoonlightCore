package de.realleoxian.moonlightcore.api.config.internal;

import de.realleoxian.moonlightcore.api.config.schema.ConfigProperty;

public interface LoadedConfig {
    <T> T getRaw(ConfigProperty<T> property);
}
