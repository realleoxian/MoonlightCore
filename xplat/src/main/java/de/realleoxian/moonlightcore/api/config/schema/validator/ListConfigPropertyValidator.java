package de.realleoxian.moonlightcore.api.config.schema.validator;

import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public record ListConfigPropertyValidator<T>(ConfigPropertyValidator<T> elementValidator) implements ConfigPropertyValidator<List<T>> {
    @Override
    public boolean test(List<T> ts) {
        for (T t : ts) {
            if (!elementValidator.test(t)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Optional<Component> getValidValueDescription() {
        return elementValidator.getValidValueDescription();
    }
}
