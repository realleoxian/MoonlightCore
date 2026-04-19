package de.realleoxian.moonlightcore.api.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public interface EntityAttributeRegistrar {
    <T extends LivingEntity> void register(Supplier<EntityType<T>> entityType, AttributeSupplier attributes);
}
