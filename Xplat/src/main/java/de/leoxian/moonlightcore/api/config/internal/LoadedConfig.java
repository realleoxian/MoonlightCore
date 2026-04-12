package de.leoxian.moonlightcore.api.config.internal;

import de.leoxian.moonlightcore.api.config.ModConfig;
import de.leoxian.moonlightcore.api.config.schema.ConfigProperty;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface LoadedConfig {

    void save(ModConfig config);

    <T> void setRaw(ConfigProperty<T> property, T value);

    <T> T getRaw(ConfigProperty<T> property);

}
