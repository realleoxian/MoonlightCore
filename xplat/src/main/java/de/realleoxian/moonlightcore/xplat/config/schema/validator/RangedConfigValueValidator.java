package de.realleoxian.moonlightcore.xplat.config.schema.validator;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueValidator;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public record RangedConfigValueValidator<T extends Number>(T min, T max) implements ConfigValueValidator<T> {
    @Override
    public boolean test(T t) {
        double val = t.doubleValue();
        return val >= min().doubleValue() && val <= max().doubleValue();
    }

    @Override
    public Optional<Component> getValidValueDescription() {
        return Optional.of(Component.translatable("moonlightcore.config.validator.ranged", min(), max()));
    }
}
