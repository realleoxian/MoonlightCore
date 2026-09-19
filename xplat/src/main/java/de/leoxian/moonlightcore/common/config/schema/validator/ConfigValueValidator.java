package de.leoxian.moonlightcore.common.config.schema.validator;

import java.util.Optional;

public interface ConfigValueValidator<T> {
    boolean test(T value);

    /// @return A description of how would be a valid value for the validator, or [Optional#empty()]
    Optional<String> getValidValueDescription();
}
