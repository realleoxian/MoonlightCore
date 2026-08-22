package de.leoxian.moonlightcore.neoforge.common.entity;

import de.leoxian.moonlightcore.common.entity.EntityAttributeRegistrar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NeoforgeEntityAttributeRegistrar implements EntityAttributeRegistrar {
    private final List<Registration<?>> registrations = new ArrayList<>();

    @SubscribeEvent
    public void onCreateEntityAttributes(EntityAttributeCreationEvent event) {
        this.registrations.forEach(reg -> reg.register(event));
        this.registrations.clear();
    }

    @Override
    public <E extends LivingEntity> void register(Supplier<EntityType<E>> entityType, AttributeSupplier attributes) {
        this.registrations.add(new Registration<>(entityType, attributes));
    }

    private record Registration<E extends LivingEntity>(Supplier<EntityType<E>> entityType, AttributeSupplier attributes) {
        void register(EntityAttributeCreationEvent event) {
            event.put(entityType.get(), attributes);
        }
    }
}
