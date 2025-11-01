package de.leoxian.moonlightcore.lookup;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.Objects;

@ApiStatus.NonExtendable
public class ApiProviderMap<K, V> {

    public static <K, V> ApiProviderMap<K, V> create() {
        return new ApiProviderMap<>();
    }

    private volatile Map<K, V> lookups = new Reference2ObjectOpenHashMap<>();

    private ApiProviderMap() {}

    public V get(K key) {
        Objects.requireNonNull(key, "Key may not be null");
        return this.lookups.get(key);
    }

    public synchronized V putIfAbsent(K key, V provider) {
        Objects.requireNonNull(key, "Key may not be null");
        Objects.requireNonNull(provider, "Provider may not be null");

        Map<K, V> lookupCopy = new Reference2ObjectOpenHashMap<>(this.lookups);
        V result = lookupCopy.putIfAbsent(key, provider);
        this.lookups = lookupCopy;

        return result;
    }
}
