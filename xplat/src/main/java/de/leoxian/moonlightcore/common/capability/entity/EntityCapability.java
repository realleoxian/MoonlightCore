package de.leoxian.moonlightcore.common.capability.entity;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface EntityCapability<A, C extends @Nullable Object> {
    static <A, C extends @Nullable Object> EntityCapability<A, C> get(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        return XplatAbstraction.INSTANCE.getEntityCapability(id, apiClass, contextClass);
    }

    @Nullable
    A find(Entity entity, C context);

    <E extends Entity> void registerForEntity(Supplier<EntityType<E>> entityType, BiFunction<E, C, @Nullable A> provider);

    void registerSelf(Supplier<EntityType<?>> entityType);

    void registerFallback(EntityCapability.Provider<A, C> provider);

    EntityCapability.@Nullable Provider<A, C> getProvider(Supplier<EntityType<?>> entityType);

    Identifier id();

    Class<A> apiClass();

    Class<C> contextClass();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {
        @Nullable
        A find(Entity entity, C context);
    }
}
