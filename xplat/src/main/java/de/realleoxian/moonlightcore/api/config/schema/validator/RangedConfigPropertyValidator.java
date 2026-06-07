package de.realleoxian.moonlightcore.api.config.schema.validator;

import net.minecraft.network.chat.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public record RangedConfigPropertyValidator<T extends Comparable<T>>(Class<T> valueType, Function<T, String> stringRepresentationFunc, T min, T max) implements ConfigPropertyValidator<Object> {
    public RangedConfigPropertyValidator(Class<T> valueType, T min, T max) {
        this(valueType, Objects::toString, min, max);
    }

    @Override
    public boolean test(Object object) {
        if (isNumber(object)) {
            double value = ((Number) object).doubleValue();
            return value >= ((Number) min).doubleValue() && value <= ((Number) max).doubleValue();
        } else if (valueType().isInstance(object)) {
            final var typed = this.valueType.cast(object);
            return typed.compareTo(min) >= 0 && typed.compareTo(max) <= 0;
        }

        throw new IllegalStateException("Invalid type, cannot be check");
    }

    @Override
    public Optional<Component> getValidValueDescription() {
        return Optional.of(Component.translatable("moonlightcore.config.validator.ranged", stringRepresentationFunc.apply(this.min), stringRepresentationFunc.apply(this.max)));
    }

    private boolean isNumber(Object object) {
        return object instanceof Number && Number.class.isAssignableFrom(this.valueType);
    }
}
