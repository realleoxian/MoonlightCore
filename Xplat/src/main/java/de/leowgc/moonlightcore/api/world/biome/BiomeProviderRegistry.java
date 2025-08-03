package de.leowgc.moonlightcore.api.world.biome;

import de.leowgc.moonlightcore.world.biome.BiomeProviderRegistryImpl;

public interface BiomeProviderRegistry {

    static BiomeProviderRegistry get(Dimension dimension) {
        return BiomeProviderRegistryImpl.get(dimension);
    }

    void addProvider(BiomeProvider provider, int weight);

    enum Dimension {
        OVERWORLD,
        NETHER
    }
}
