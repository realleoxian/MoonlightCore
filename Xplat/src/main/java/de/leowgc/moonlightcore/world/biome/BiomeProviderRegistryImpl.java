package de.leowgc.moonlightcore.world.biome;

import com.google.common.base.Preconditions;
import de.leowgc.moonlightcore.api.world.biome.BiomeProvider;
import de.leowgc.moonlightcore.api.world.biome.BiomeProviderRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class BiomeProviderRegistryImpl implements BiomeProviderRegistry {
    private static final Map<Dimension, BiomeProviderRegistryImpl> REGISTRIES = new HashMap<>();

    public static BiomeProviderRegistryImpl get(Dimension dimension) {
        return REGISTRIES.computeIfAbsent(dimension, BiomeProviderRegistryImpl::new);
    }

    private static final int VANILLA_PROVIDER_ID = 0;

    private final Int2ObjectMap<ProviderEntry> providers = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<ProviderEntry> providerIds = new Object2IntArrayMap<>();

    private final AtomicInteger providerId = new AtomicInteger(1);

    @SuppressWarnings("FieldCanBeLocal")
    private final Dimension dimension;

    BiomeProviderRegistryImpl(Dimension dimension) {
        this.dimension = dimension;

        if(this.dimension == Dimension.OVERWORLD) {
            this.providers.put(VANILLA_PROVIDER_ID, new ProviderEntry(new VanillaOverworldBiomeProvider(), 1000));
        } else if(this.dimension == Dimension.NETHER) {
            this.providers.put(VANILLA_PROVIDER_ID, new ProviderEntry(new VanillaNetherBiomeProvider(), 1000));
        }
    }

    @Override
    public void addProvider(BiomeProvider provider, int weight) {
        Preconditions.checkNotNull(provider, "Biome provider can't be null.");

        int providerId = this.providerId.getAndIncrement();
        ProviderEntry entry = new ProviderEntry(provider, weight);

        this.providers.put(providerId, entry);
        this.providerIds.put(entry, providerId);
    }

    @ApiStatus.Internal
    public Optional<ProviderEntry> getProvider(int id) {
        return Optional.ofNullable(this.providers.get(id));
    }

    public int getProviderId(BiomeProvider provider) {
        return this.providerIds.getInt(provider);
    }

    public Collection<ProviderEntry> getEntries() {
        return this.providers.values();
    }

    public int getProviderCount() {
        return this.providers.size();
    }

    public record ProviderEntry(BiomeProvider provider, int weight) {}
}
