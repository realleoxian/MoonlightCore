package de.leoxian.moonlightcore.impl.apilookup;

import de.leoxian.moonlightcore.api.apilookup.ApiLookupRegistry;
import de.leoxian.moonlightcore.api.apilookup.entity.EntityApiLookup;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;

import java.util.*;

public final class EntityApiLookupImpl<A, C extends @Nullable Object> extends ApiLookupImpl<A, C> implements EntityApiLookup<A, C> {
    private static final ApiLookupRegistry<EntityApiLookup<?, ?>> REGISTRY = ApiLookupRegistry.create(EntityApiLookupImpl::new);

    @SuppressWarnings("unchecked")
    public static <A, C extends @Nullable Object> EntityApiLookup<A, C> find(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        return (EntityApiLookup<A, C>) REGISTRY.create(name, apiClass, contextClass);
    }

    private final Map<EntityType<?>, EntityApiLookup.Provider<A, C>> providers = new IdentityHashMap<>();
    private final List<EntityApiLookup.Provider<A, C>> fallbackProviders = new ArrayList<>();

    private EntityApiLookupImpl(ResourceLocation name, Class<A> apiClass, Class<C> contextClass) {
        super(name, apiClass, contextClass);
    }

    @Override
    public @Nullable A find(Entity entity, C context) {
        Objects.requireNonNull(entity, "Entity cannot be 'null'");

        if(!EntitySelector.ENTITY_STILL_ALIVE.test(entity)) {
            return null;
        }

        EntityApiLookup.Provider<A, C> provider = providers.get(entity.getType());
        if(provider == null) {
            return null;
        }

        A instance = provider.get(entity, context);
        if(instance == null) {
            for(EntityApiLookup.Provider<A, C> fallback : fallbackProviders) {
                instance = fallback.get(entity, context);

                if(instance != null) {
                    break;
                }
            }
        }

        return instance;
    }

    @Override
    public void register(Provider<A, C> provider, EntityType<?>... entityTypes) {
        Objects.requireNonNull(provider, "Entity API provider cannot be 'null'");

        if(entityTypes.length == 0) {
            throw new IllegalArgumentException("Must register at least one EntityType instance with an EntityApiLookup$Provider");
        } else {
            for(EntityType<?> entityType : entityTypes) {
                Objects.requireNonNull(entityType, "EntityType cannot be 'null'");

                if(providers.putIfAbsent(entityType, provider) != null) {
                    throw new IllegalStateException("Duplicated API encountered on entity type '" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType) + "'");
                }
            }
        }
    }

    @Override
    public void registerFallback(Provider<A, C> provider) {
        Objects.requireNonNull(provider, "Fallback EntityApiLookup provider may not be 'null'");
        fallbackProviders.add(provider);
    }

    @Override
    public @Nullable Provider<A, C> getProvider(EntityType<?> entityType) {
        Objects.requireNonNull(entityType, "EntityType cannot be 'null'");
        return providers.get(entityType);
    }

    @Override
    public List<Provider<A, C>> getFallbackProviders() {
        return Collections.unmodifiableList(fallbackProviders);
    }
}
