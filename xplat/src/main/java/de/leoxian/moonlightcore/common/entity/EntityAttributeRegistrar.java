package de.leoxian.moonlightcore.common.entity;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface EntityAttributeRegistrar {
    static void init(String namespace, Consumer<EntityAttributeRegistrar> initializer) {
        XplatAbstraction.INSTANCE.entityAttributes(namespace, initializer);
    }

    <E extends LivingEntity> void register(Supplier<EntityType<E>> entityType, AttributeSupplier attributes);
}
