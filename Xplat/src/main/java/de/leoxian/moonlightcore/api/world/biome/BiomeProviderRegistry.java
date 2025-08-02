package de.leoxian.moonlightcore.api.world.biome;

import de.leoxian.moonlightcore.world.biome.BiomeProviderRegistryImpl;

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
