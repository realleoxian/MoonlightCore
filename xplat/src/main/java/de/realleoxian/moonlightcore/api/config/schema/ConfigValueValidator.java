package de.realleoxian.moonlightcore.api.config.schema;

import net.minecraft.network.chat.Component;

import java.util.Optional;

public interface ConfigValueValidator<T> {
    boolean test(T t);

    Optional<Component> getValidValueDescription();
}
