package de.leoxian.moonlightcore.common.capability.entity;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface EntityCapability<A, C extends @Nullable Object> {
    /// Creates a new entity capability, or gets it if it already exists
    /// @param id The id of the capability
    /// @param apiClass The type of required API
    /// @param contextClass The type of required context
    /// @throws IllegalArgumentException If another `apiClass` or another `contextClass` was already registered with the same id
    static <A, C extends @Nullable Object> EntityCapability<A, C> create(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return XplatAbstraction.INSTANCE.createEntityCapability(id, apiClass, contextClass);
    }

    /// Attempt to retrieve an instance of this capability from the given entity
    /// @param entity The target entity
    /// @param context Additional context for the query
    /// @return The capability instance from the entity, or `null` if it couldn't be found
    @Nullable
    A find(Entity entity, C context);

    /// Registers a provider of this capability to the given entity type
    /// @param entityType The entity type
    /// @param provider The instance provider
    <E extends Entity> void registerForEntity(Supplier<EntityType<E>> entityType, BiFunction<E, C, @Nullable A> provider);

    /// Self-Registers the capability provider from the given entity. This can be used if the entity registers the API itself
    /// @param entityType The entity type
    void registerSelf(Supplier<EntityType<?>> entityType);

    /// Registers a provider of this capability that can be used on all entities on registry
    /// @param provider The fallback provider
    void registerFallback(EntityCapability.Provider<A, C> provider);

    /// Retrieves the provider registered to the given block entity type
    /// @param entityType The entity type
    /// @return The capability instance provider used for the entity type, or `null` if there is nothing registered
    EntityCapability.@Nullable Provider<A, C> getProvider(Supplier<EntityType<?>> entityType);

    /// @return This capability's id
    Identifier id();

    /// @return This capability's API type
    Class<A> apiClass();

    /// @return This capability's context type
    Class<C> contextClass();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {
        @Nullable
        A find(Entity entity, C context);
    }
}
