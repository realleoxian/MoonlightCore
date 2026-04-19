package de.realleoxian.moonlightcore.fabric.entity;

import de.realleoxian.moonlightcore.api.entity.EntityAttributeRegistrar;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public class FabricEntityAttributeRegistrarImpl implements EntityAttributeRegistrar {
    @Override
    public <T extends LivingEntity> void register(Supplier<EntityType<T>> entityType, AttributeSupplier attributes) {
        FabricDefaultAttributeRegistry.register(entityType.get(), attributes);
    }
}
