package de.leoxian.moonlightcore.fabric.common.entity;

import de.leoxian.moonlightcore.common.entity.EntityAttributeRegistrar;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.util.function.Supplier;

public enum FabricEntityAttributeRegistrar implements EntityAttributeRegistrar {
    INSTANCE
    ;

    @Override
    public <E extends LivingEntity> void register(Supplier<EntityType<E>> entityType, AttributeSupplier attributes) {
        FabricDefaultAttributeRegistry.register(entityType.get(), attributes);
    }
}
