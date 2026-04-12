package de.realleoxian.moonlightcore.impl.config.schema.validator;

import de.realleoxian.moonlightcore.api.config.schema.ConfigPropertyValidator;
import net.minecraft.network.chat.Component;

public class NoOpConfigValidator implements ConfigPropertyValidator<Object> {
    @SuppressWarnings("rawtypes")
    public static final ConfigPropertyValidator INSTANCE = new NoOpConfigValidator();

    private NoOpConfigValidator() {}

    @Override
    public boolean test(Object o) {
        return true;
    }

    @Override
    public Component getValidValueDescription() {
        return Component.empty();
    }
}
