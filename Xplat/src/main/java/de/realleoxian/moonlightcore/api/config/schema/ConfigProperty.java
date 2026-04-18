package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface ConfigProperty<T> extends Supplier<T> {

    @Override
    T get();

    Supplier<T> getDefault();

    ConfigPropertyType<T> getType();

    ConfigPropertyValidator<T> getValidator();

    @Nullable Iterable<String> getComments();

    @Nullable String getTranslationKey();

    RestartType getRestartType();

    ConfigKey getKey();

}
