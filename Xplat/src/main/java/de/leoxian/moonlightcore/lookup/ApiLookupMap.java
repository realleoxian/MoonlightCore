package de.leoxian.moonlightcore.lookup;

import de.leoxian.moonlightcore.util.nullness.Nonnull;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ApiLookupMap<L> implements Iterable<L> {
    private final Map<ResourceLocation, StoredLookup<L>> lookups = new HashMap<>();
    private final LookupConstructor<L> lookupConstructor;

    public static <L> ApiLookupMap<L> create(LookupConstructor<L> lookupConstructor) {
        Objects.requireNonNull(lookupConstructor, "Lookup factory may not be null");
        return new ApiLookupMap<>(lookupConstructor);
    }

    public ApiLookupMap(LookupConstructor<L> lookupConstructor) {
        this.lookupConstructor = lookupConstructor;
    }

    public synchronized L getLookup(ResourceLocation id, Class<?> apiClass, Class<?> contextClass) {
        Objects.requireNonNull(id, "Lookup id may not be null");
        Objects.requireNonNull(apiClass, "API class may not be null");
        Objects.requireNonNull(contextClass, "Context class may not be null");

        StoredLookup<L> storedLookup = this.lookups.computeIfAbsent(id, lookupId -> new StoredLookup<>(this.lookupConstructor.get(lookupId, apiClass, contextClass), apiClass, contextClass));
        if (storedLookup.apiClass == apiClass && storedLookup.contextClass == contextClass) {
            return storedLookup.lookup();
        }

        String errorMessage = String.format(
                "Lookup with id %s is already registered with api class %s and context class %s. It can't be registered with api class %s and context class %s",
                id,
                storedLookup.apiClass.getCanonicalName(),
                storedLookup.contextClass.getCanonicalName(),
                apiClass.getCanonicalName(),
                contextClass.getCanonicalName()
        );
        throw new IllegalArgumentException(errorMessage);
    }

    @Override
    @Nonnull
    public Iterator<L> iterator() {
        return this.lookups.values().stream().map(StoredLookup::lookup).collect(Collectors.toList()).iterator();
    }

    @FunctionalInterface
    public interface LookupConstructor<L> {
        L get(ResourceLocation location, Class<?> apiClass, Class<?> contextClass);
    }

    private record StoredLookup<L>(L lookup, Class<?> apiClass, Class<?> contextClass) {}
}
