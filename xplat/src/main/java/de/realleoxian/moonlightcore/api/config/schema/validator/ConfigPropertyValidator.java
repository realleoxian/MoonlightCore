package de.realleoxian.moonlightcore.api.config.schema.validator;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public interface ConfigPropertyValidator<T> {
    ConfigPropertyValidator<?> NO_OP = new ConfigPropertyValidator<>() {
        @Override
        public boolean test(Object object) {
            return true;
        }

        @Override
        public Optional<Component> getValidValueDescription() {
            return Optional.empty();
        }
    };

    boolean test(T t);

    Optional<Component> getValidValueDescription();
}
