package de.leoxian.moonlightcore.common.config.schema.validator;

import java.util.Optional;

public interface ConfigValueValidator<T> {
    boolean test(T value);

    Optional<String> getValidValueDescription();
}
