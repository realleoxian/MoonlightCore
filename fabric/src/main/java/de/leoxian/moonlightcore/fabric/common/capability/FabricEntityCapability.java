package de.leoxian.moonlightcore.fabric.common.capability;

import de.leoxian.moonlightcore.common.capability.entity.EntityCapability;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class FabricEntityCapability<A, C> implements EntityCapability<A, C> {
    private static final Map<Identifier, FabricEntityCapability<?, ?>> CAPABILITIES = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <A, C> EntityCapability<A, C> get(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        FabricEntityCapability<?, ?> existing = CAPABILITIES.computeIfAbsent(id, key -> new FabricEntityCapability<>(key, apiClass, contextClass));

        if (existing.apiClass() != apiClass) {
            throw new IllegalStateException("Attempted to register capability " + id + " with existing type class " + existing.apiClass() + " != " + apiClass);
        }
        if (existing.contextClass() != contextClass) {
            throw new IllegalStateException("Attempted to register capability " + id + " with existing context class " + existing.contextClass() + " != " + contextClass);
        }

        return (EntityCapability<A, C>) existing;
    }

    private final EntityApiLookup<A, C> apiLookup;

    public FabricEntityCapability(Identifier id, Class<A> apiClass, Class<C> contextClass) {
        this.apiLookup = EntityApiLookup.get(id, apiClass, contextClass);
    }

    @Override
    public @Nullable A find(Entity entity, C context) {
        return apiLookup.find(entity, context);
    }

    @Override
    public <E extends Entity> void registerForEntity(Supplier<EntityType<E>> entityType, BiFunction<E, C, A> provider) {
        apiLookup.registerForType(provider, entityType.get());
    }

    @Override
    public void registerSelf(Supplier<EntityType<?>> entityType) {
        apiLookup.registerSelf(entityType.get());
    }

    @Override
    public void registerFallback(Provider<A, C> provider) {
        apiLookup.registerFallback(provider::find);
    }

    @Override
    public @Nullable Provider<A, C> getProvider(Supplier<EntityType<?>> entityType) {
        EntityApiLookup.EntityApiProvider<A, C> provider = apiLookup.getProvider(entityType.get());
        if (provider == null) {
            return null;
        }
        return provider::find;
    }

    @Override
    public Identifier id() {
        return apiLookup.getId();
    }

    @Override
    public Class<A> apiClass() {
        return apiLookup.apiClass();
    }

    @Override
    public Class<C> contextClass() {
        return apiLookup.contextClass();
    }
}
