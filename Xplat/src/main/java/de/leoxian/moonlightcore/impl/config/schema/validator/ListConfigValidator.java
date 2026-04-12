package de.leoxian.moonlightcore.impl.config.schema.validator;

import de.leoxian.moonlightcore.api.config.schema.ConfigPropertyValidator;
import net.minecraft.network.chat.Component;

import java.util.List;

public record ListConfigValidator<E>(ConfigPropertyValidator<E> validator) implements ConfigPropertyValidator<List<E>> {

    @Override
    public boolean test(List<E> es) {
        return es.stream().allMatch(validator::test);
    }

    @Override
    public Component getValidValueDescription() {
        return validator.getValidValueDescription();
    }

}
