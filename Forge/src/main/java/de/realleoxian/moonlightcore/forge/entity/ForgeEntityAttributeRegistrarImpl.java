package de.realleoxian.moonlightcore.forge.entity;

import de.realleoxian.moonlightcore.api.entity.EntityAttributeRegistrar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ForgeEntityAttributeRegistrarImpl implements EntityAttributeRegistrar {
    private final List<Registration<?>> registrations = new ArrayList<>();

    @SubscribeEvent
    private void onRegisterAttributesEvent(EntityAttributeCreationEvent event) {
        this.registrations.forEach(r -> r.register(event));
    }

    @Override
    public <T extends LivingEntity> void register(Supplier<EntityType<T>> entityType, AttributeSupplier attributes) {
        this.registrations.add(new Registration<>(entityType, attributes));
    }

    private record Registration<T extends LivingEntity>(Supplier<EntityType<T>> entityType, AttributeSupplier attributes) {
        void register(EntityAttributeCreationEvent event) {
            event.put(entityType().get(), attributes);
        }
    }
}
