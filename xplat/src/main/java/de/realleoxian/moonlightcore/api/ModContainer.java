package de.realleoxian.moonlightcore.api;

import de.realleoxian.moonlightcore.api.command.CommandRegistrar;
import de.realleoxian.moonlightcore.api.entity.EntityAttributeRegistrar;

import java.util.function.Consumer;

public interface ModContainer {
    String namespace();

    default void commands(CommandRegistrar initializer) {
        MoonlightCore.ABSTRACTION.commands(namespace(), initializer);
    }

    default void entityAttributes(Consumer<EntityAttributeRegistrar> initializer) {
        MoonlightCore.ABSTRACTION.entityAttributes(namespace(), initializer);
    }

    default void argumentType(String namespace, Consumer<EntityAttributeRegistrar> initializer) {
        MoonlightCore.ABSTRACTION.argumentType(namespace, initializer);
    }
}
