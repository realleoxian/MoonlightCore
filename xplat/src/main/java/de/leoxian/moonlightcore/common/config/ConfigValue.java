package de.leoxian.moonlightcore.common.config;

import de.leoxian.moonlightcore.common.config.schema.ConfigKey;
import de.leoxian.moonlightcore.common.config.schema.RestartType;
import de.leoxian.moonlightcore.common.config.schema.type.ConfigValueType;
import de.leoxian.moonlightcore.common.config.schema.validator.ConfigValueValidator;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.NonExtendable
public interface ConfigValue<T> {
    T get();

    T defaultValue();

    ConfigValueType<T> type();

    ConfigValueValidator<T> validator();

    RestartType requiredRestartType();

    ConfigKey key();

    @Nullable
    Iterable<String> comments();

    @Nullable
    String translationKey();
}
