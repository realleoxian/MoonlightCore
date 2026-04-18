package de.realleoxian.moonlightcore.api.apilookup.entity;

import de.realleoxian.moonlightcore.api.apilookup.ApiLookup;
import de.realleoxian.moonlightcore.impl.apilookup.EntityApiLookupImpl;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public interface EntityApiLookup<A, C extends @Nullable Object> extends ApiLookup<A, C> {
    static <A, C extends @Nullable Object> EntityApiLookup<A, C> find(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return EntityApiLookupImpl.find(name, apiClass, contextClass);
    }

    @Nullable A find(Entity entity, C context);

    void register(EntityApiLookup.Provider<A, C> provider, EntityType<?>... entityTypes);

    void registerFallback(EntityApiLookup.Provider<A, C> provider);

    EntityApiLookup.@Nullable Provider<A, C> getProvider(EntityType<?> entityType);

    List<Provider<A, C>> getFallbackProviders();

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {
        @Nullable A get(Entity entity, C context);
    }
}

