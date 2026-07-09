package de.leoxian.moonlightcore.common.config.schema.validator;

import java.util.Optional;

public record RangedConfigValidator<T extends Number>(T min, T max) implements ConfigValueValidator<T> {
    @Override
    public boolean test(T value) {
        double dValue = value.doubleValue();
        return dValue >= min.doubleValue() && dValue <= max.doubleValue();
    }

    @Override
    public Optional<String> getValidValueDescription() {
        return Optional.of("[" + min + ", " + max + "]");
    }
}
