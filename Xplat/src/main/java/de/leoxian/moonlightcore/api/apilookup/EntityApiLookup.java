package de.leoxian.moonlightcore.api.apilookup;

import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import de.leoxian.moonlightcore.impl.apilookup.EntityApiLookupImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public interface EntityApiLookup<A, C extends @Nullable Object> extends ApiLookup<A, C> {

    static <A, C extends @Nullable Object> EntityApiLookup<A, C> get(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return EntityApiLookupImpl.get(name, apiClass, contextClass);
    }

    @Nullable
    A get(Entity entity, C context);

    void register(EntityApiLookup.Provider<A, C> provider, EntityType<?>... entityTypes);

    EntityApiLookup.@Nullable Provider<A, C> getProvider(EntityType<?> entityType);

    @FunctionalInterface
    interface Provider<A, C extends @Nullable Object> {

        @Nullable
        A get(Entity entity, C context);

    }

}
