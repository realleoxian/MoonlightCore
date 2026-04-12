package de.realleoxian.moonlightcore.api.config.schema;

import de.realleoxian.moonlightcore.impl.config.schema.validator.ConfigValidatorImpl;
import de.realleoxian.moonlightcore.impl.config.schema.validator.RangedConfigValidator;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public interface ConfigPropertyValidator<T> extends Predicate<T> {

    @SuppressWarnings("unchecked")
    static <T extends Comparable<T>> ConfigPropertyValidator<T> ranged(Class<T> clazz, T min, T max) {
        return (ConfigPropertyValidator<T>) (Object) new RangedConfigValidator<>(clazz, min, max);
    }

    static <T> ConfigPropertyValidator<T> of(Predicate<T> filter, Component validValueDescription) {
        return new ConfigValidatorImpl<>(filter, validValueDescription);
    }

    static <T> ConfigPropertyValidator<T> of(Predicate<T> filter) {
        return new ConfigValidatorImpl<>(filter);
    }

    Component getValidValueDescription();

}
