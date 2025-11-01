package de.leoxian.moonlightcore.lookup.entity;

import com.mojang.logging.LogUtils;
import de.leoxian.moonlightcore.lookup.ApiLookupMap;
import de.leoxian.moonlightcore.lookup.ApiProviderMap;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntityApiLookup<A, C> {
    private static final ApiLookupMap<EntityApiLookup<?, ?>> LOOKUPS = ApiLookupMap.create(EntityApiLookup::new);

    private static final Map<Class<?>, Set<EntityType<?>>> REGISTERED_SELVES = new HashMap<>();
    private static boolean checkEntityLookup = true;

    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("unchecked")
    public static <A, C> EntityApiLookup<A, C> get(ResourceLocation id, Class<A> apiClass, Class<C> contextClass) {
        return (EntityApiLookup<A, C>) LOOKUPS.getLookup(id, apiClass, contextClass);
    }

    private final ApiProviderMap<EntityType<?>, EntityApiProvider<A, C>> providersMap = ApiProviderMap.create();
    private final List<EntityApiProvider<A, C>> fallbackProviders = new CopyOnWriteArrayList<>();

    private final ResourceLocation id;
    private final Class<A> apiClass;
    private final Class<C> contextClass;

    private EntityApiLookup(ResourceLocation id, Class<A> apiClass, Class<C> contextClass) {
        this.id = id;
        this.apiClass = apiClass;
        this.contextClass = contextClass;
    }

    public static void checkSelfImplementingTypes(MinecraftServer server) {
        if(checkEntityLookup) {
            checkEntityLookup = false;

            synchronized (REGISTERED_SELVES) {
                REGISTERED_SELVES.forEach((apiClass, entityTypes) -> {
                    for(EntityType<?> entityType : entityTypes) {
                        Entity entity = entityType.create(server.overworld());

                        if(entity == null) {
                            String errorMessage = String.format(
                                    "Failed to register self-implementing entities for API class %s. Can not create entity of type %s",
                                    apiClass.getCanonicalName(),
                                    BuiltInRegistries.ENTITY_TYPE.getId(entityType)
                            );
                            throw new NullPointerException(errorMessage);
                        }

                        if(!apiClass.isInstance(entity)) {
                            String errorMessage = String.format(
                                    "Failed to register self-implementing entitie. API class %s is not assignable from entity class %s",
                                    apiClass.getCanonicalName(),
                                    entity.getClass().getCanonicalName()
                            );

                            throw new IllegalArgumentException(errorMessage);
                        }
                    }
                });
            }
        }
    }

    public A find(Entity entity, C context) {
        Objects.requireNonNull(entity, "Entity may not be null");

        if(EntitySelector.ENTITY_STILL_ALIVE.test(entity)) {
            EntityApiProvider<A, C> provider = this.providersMap.get(entity.getType());

            if(provider != null) {
                A instance = provider.find(entity, context);

                if(instance != null) {
                    return instance;
                }
            }

            for(EntityApiProvider<A, C> fallback : this.fallbackProviders) {
                A instance = fallback.find(entity, context);

                if(instance != null) {
                    return instance;
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public void registerSelf(EntityType<?>... entityTypes) {
        synchronized (REGISTERED_SELVES) {
            REGISTERED_SELVES.computeIfAbsent(this.apiClass, c -> new LinkedHashSet<>()).addAll(Arrays.asList(entityTypes));
        }

        registerForEntityTypes((entity, ctx) -> (A) entity, entityTypes);
    }

    public void registerForEntityTypes(EntityApiProvider<A, C> provider, EntityType<?>... entityTypes) {
        Objects.requireNonNull(provider, "EntityApiProvider may not be null");

        if(entityTypes.length == 0) {
            throw new IllegalArgumentException("Must register at least one entity type instance with an EntityApiProvider");
        }

        for(EntityType<?> entityType : entityTypes) {
            if(this.providersMap.putIfAbsent(entityType, provider) != null) {
                LOGGER.warn("Encountered duplicated API provider registration for entity type: {}", BuiltInRegistries.ENTITY_TYPE.getId(entityType));
            }
        }
    }

    public void registerFallback(EntityApiProvider<A, C> fallback) {
        Objects.requireNonNull(fallback, "EntityApiProvider may not be null");
        this.fallbackProviders.add(fallback);
    }

    @Nullable
    public EntityApiProvider<A, C> getProvider(EntityType<?> entity) {
        return this.providersMap.get(entity);
    }

    public ResourceLocation id() {
        return this.id;
    }

    public Class<A> apiClass() {
        return this.apiClass;
    }

    public Class<C> contextClass() {
        return this.contextClass;
    }

    public interface EntityApiProvider<A, C> {
        @Nullable
        A find(Entity entity, C context);
    }
}
