package de.realleoxian.moonlightcore.xplat.config.schema.validator;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueValidator;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public record ListConfigValueValidator<T>(ConfigValueValidator<T> elementValidator) implements ConfigValueValidator<List<T>> {
    @Override
    public boolean test(List<T> ts) {
        return ts.stream().allMatch(elementValidator::test);
    }

    @Override
    public Optional<Component> getValidValueDescription() {
        return elementValidator.getValidValueDescription();
    }
}
