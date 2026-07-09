package de.leoxian.moonlightcore.common.config.file;

import de.leoxian.moonlightcore.common.config.ConfigSchema;
import de.leoxian.moonlightcore.common.config.ConfigValue;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface LoadedConfig {
    <T> T getRaw(ConfigValue<T> configValue);

    <T> void setRaw(ConfigValue<T> configValue, T newValue);

    @SuppressWarnings({"unchecked", "rawtypes"})
    default void applyFrom(ConfigSchema schema, LoadedConfig loadedConfig) {
        for (final var configValue : schema.getSchemas())
            setRaw((ConfigValue) configValue, loadedConfig.getRaw((ConfigValue) configValue));
        for (final var child : schema.getSchemas())
            applyFrom(child, loadedConfig);
    }
}
