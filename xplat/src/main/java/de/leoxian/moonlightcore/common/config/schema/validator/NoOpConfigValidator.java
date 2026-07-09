package de.leoxian.moonlightcore.common.config.schema.validator;

import java.util.Optional;

public enum NoOpConfigValidator implements ConfigValueValidator<Object> {
    INSTANCE
    ;

    @Override
    public boolean test(Object value) {
        return true;
    }

    @Override
    public Optional<String> getValidValueDescription() {
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public <T> ConfigValueValidator<T> cast() {
        return (ConfigValueValidator<T>) this;
    }
}
