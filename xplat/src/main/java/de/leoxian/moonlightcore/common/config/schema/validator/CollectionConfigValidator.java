package de.leoxian.moonlightcore.common.config.schema.validator;

import java.util.Collection;
import java.util.Optional;

public record CollectionConfigValidator<E, C extends Collection<E>>(ConfigValueValidator<E> elementValidator) implements ConfigValueValidator<C> {
    @Override
    public boolean test(C value) {
        return value.stream().allMatch(elementValidator::test);
    }

    @Override
    public Optional<String> getValidValueDescription() {
        return elementValidator.getValidValueDescription();
    }
}
