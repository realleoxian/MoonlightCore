package de.realleoxian.moonlightcore.impl.apilookup;

import de.realleoxian.moonlightcore.api.apilookup.ApiLookupRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class ApiLookupRegistryImpl<A> implements ApiLookupRegistry<A> {

    public static <A> ApiLookupRegistry<A> create(ApiLookupRegistry.LookupFactory<A> lookupFactory) {
        return new ApiLookupRegistryImpl<>(lookupFactory);
    }

    private final ConcurrentHashMap<ResourceLocation, StoredLookup<A>> lookups = new ConcurrentHashMap<>();
    private final ApiLookupRegistry.LookupFactory<A> lookupFactory;

    private ApiLookupRegistryImpl(LookupFactory<A> lookupFactory) {
        this.lookupFactory = lookupFactory;
    }

    @Override
    public A create(ResourceLocation name, Class<?> apiClass, Class<?> contextClass) {
        Objects.requireNonNull(name, "API name cannot be 'null'");
        Objects.requireNonNull(apiClass, "API class cannot be 'null'");
        Objects.requireNonNull(contextClass, "API context class cannot be 'null'");

        StoredLookup<A> stored = lookups.computeIfAbsent(name, n -> new StoredLookup<A>(
                lookupFactory.create(n, apiClass, contextClass),
                apiClass, contextClass));

        if(stored.apiClass != apiClass) {
            throw new IllegalArgumentException("API Lookup with name '%s' was already registered with API class %s".formatted(name, apiClass));
        } else if (stored.contextClass != contextClass) {
            throw new IllegalArgumentException("API Lookup with name '%s' was already registered with context class %s".formatted(name, contextClass));
        }

        return stored.instance;
    }

    @Override
    public @UnmodifiableView List<A> getAPIs() {
        return lookups.values().stream().map(StoredLookup::instance).toList();
    }

    private record StoredLookup<A>(A instance, Class<?> apiClass, Class<?> contextClass) {}

}
