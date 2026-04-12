package de.realleoxian.moonlightcore.impl.config.schema.validator;

import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyValidator;
import net.minecraft.network.chat.Component;

public record RangedConfigValidator<E extends Comparable<E>>(Class<E> clazz, E min, E max) implements ConfigPropertyValidator<Object> {

    public RangedConfigValidator {
        if (max.compareTo(min) < 0) {
            throw new IllegalArgumentException("Max value cannot be less than the minimum value");
        }
    }

    @Override
    public boolean test(Object o) {
        if (isNumber(o)) {
            double d = ((Number) o).doubleValue();
            double min = ((Number) min()).doubleValue();
            double max = ((Number) max()).doubleValue();

            return d >= min && d <= max;
        } else if (!clazz().isInstance(o)) {
            return false;
        }

        E e = clazz().cast(o);
        return e.compareTo(min()) >= 0 && e.compareTo(max()) <= 0;
    }

    @Override
    public Component getValidValueDescription() {
        return Component.translatable("moonlightcore.config.validator.ranged", min().toString(), max().toString());
    }

    private boolean isNumber(Object e) {
        return Number.class.isAssignableFrom(clazz) && e instanceof Number;
    }

}
