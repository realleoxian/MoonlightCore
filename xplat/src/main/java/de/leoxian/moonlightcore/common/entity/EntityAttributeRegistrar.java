package de.leoxian.moonlightcore.common.entity;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface EntityAttributeRegistrar {
    /// Configure and register the default attributes of modded entities
    /// @param namespace The mod's id
    /// @param initializer The initializer
    static void configure(String namespace, Consumer<EntityAttributeRegistrar> initializer) {
        XplatAbstraction.INSTANCE.entityAttributes(namespace, initializer);
    }

    /// Registers a default attributes to the given entity type
    /// @param entityType The entity type
    /// @param attributes The default attributes
    <E extends LivingEntity> void register(Supplier<EntityType<E>> entityType, AttributeSupplier attributes);
}
