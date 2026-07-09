package de.leoxian.moonlightcore.common.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public interface EntityAttributeRegistrar {
    <E extends LivingEntity> void register(Supplier<EntityType<E>> entityType, AttributeSupplier attributes);
}
