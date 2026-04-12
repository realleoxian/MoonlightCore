package de.realleoxian.moonlightcore.impl.config.schema.validator;

import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyValidator;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public record ConfigValidatorImpl<T>(Predicate<T> filter, Component validValueDescription) implements ConfigPropertyValidator<T> {

    public ConfigValidatorImpl(Predicate<T> filter) {
        this(filter, Component.empty());
    }

    @Override
    public boolean test(T t) {
        return filter.test(t);
    }

    @Override
    public Component getValidValueDescription() {
        return validValueDescription;
    }

}
