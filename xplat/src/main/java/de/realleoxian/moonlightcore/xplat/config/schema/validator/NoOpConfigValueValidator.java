package de.realleoxian.moonlightcore.xplat.config.schema.validator;

import de.realleoxian.moonlightcore.api.config.schema.ConfigValueValidator;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public enum NoOpConfigValueValidator implements ConfigValueValidator<Object> {
    INSTANCE
    ;

    @Override
    public boolean test(Object object) {
        return true;
    }

    @Override
    public Optional<Component> getValidValueDescription() {
        return Optional.empty();
    }

    @SuppressWarnings({"unchecked"})
    public <T> ConfigValueValidator<T> cast() {
        return (ConfigValueValidator<T>) this;
    }
}
