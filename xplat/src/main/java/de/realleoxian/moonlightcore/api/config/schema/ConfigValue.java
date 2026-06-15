package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.api.config.ConfigKey;
import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataHolder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ConfigValue<T> extends ConfigMetadataHolder {
    void set(T t, boolean shouldSave);

    void registerListener(ChangeListener<T> listener);

    T getValue();

    T getDefaultValue();

    ConfigValueSerializer<T> getSerializer();

    ConfigValueValidator<T> getValidator();

    boolean requiresWorldRestart();

    boolean requiresGameRestart();

    ConfigKey getKey();

    @FunctionalInterface
    interface ChangeListener<T> {
        void onConfigValueChange(final T oldValue, final T newValue);
    }
}
