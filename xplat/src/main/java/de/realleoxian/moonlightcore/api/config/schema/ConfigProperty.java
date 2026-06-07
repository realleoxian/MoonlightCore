package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.api.config.metadata.ConfigMetadataHolder;
import de.realleoxian.moonlightcore.api.config.schema.validator.ConfigPropertyValidator;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ApiStatus.NonExtendable
public interface ConfigProperty<T> extends ConfigMetadataHolder {
    T value();

    ConfigKey key();

    ConfigPropertyType<T> type();

    ConfigPropertyValidator<T> validator();

    Supplier<T> defaultValue();

    RestartType restartType();

    interface Builder<T> extends ConfigMetadataHolder.Builder<ConfigProperty.Builder<T>> {
        Builder<T> worldRestart();

        Builder<T> gameRestart();

        Builder<T> validator(ConfigPropertyValidator<T> validator);

        default Builder<T> validator(Predicate<T> filter, @Nullable Component validValueDescription) {
            return validator(new ConfigPropertyValidator<T>() {
                @Override
                public boolean test(T t) {
                    return filter.test(t);
                }

                @Override
                public Optional<Component> getValidValueDescription() {
                    return Optional.ofNullable(validValueDescription);
                }
            });
        }

        default Builder<T> validator(Predicate<T> filter) {
            return validator(filter, null);
        }
    }
}
