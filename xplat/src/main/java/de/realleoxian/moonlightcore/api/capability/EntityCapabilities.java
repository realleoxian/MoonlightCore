package de.realleoxian.moonlightcore.api.capability;

import de.realleoxian.moonlightcore.api.MoonlightCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface EntityCapabilities {
    static EntityCapabilities get() {
        return MoonlightCore.ABSTRACTION.getEntityCapabilities();
    }

    @Nullable
    <T, C> T find(Entity entity, C context);

    <T, C> CapabilityType<Entity, T, C> create(ResourceLocation name, Class<T> capabilityType, Class<C> contextType);

    <T, C> void registerForTypes(CapabilityType<Entity, T, C> capabilityType, Provider<T, C> provider, Supplier<EntityType<?>>... entityTypes);

    @SuppressWarnings({"unchecked", "rawtypes"})
    default <E extends Entity, T, C> void registerForType(CapabilityType<Entity, T, C> capabilityType, BiFunction<Entity, C, @Nullable T> provider, Supplier<EntityType<E>> entityType) {
        registerForTypes(capabilityType, provider::apply, (Supplier<EntityType<?>>) (Supplier) entityType);
    }

    <T, C> void registerSelf(CapabilityType<Entity, T, C> capabilityType, Supplier<EntityType<?>>... entityTypes);

    <T, C> void registerFallback(CapabilityType<Entity, T, C> capabilityType, Provider<T, C> provider);

    interface Provider<T, C> {
        @Nullable
        T find(Entity entity, C context);
    }
}
