package de.leoxian.moonlightcore.api.world.biome;

import de.leoxian.moonlightcore.world.biome.EndBiomeRegistryImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public interface EndBiomeRegistry {

    static void addHighlandBiome(ResourceKey<Biome> moddedBiome, int weight) {
        EndBiomeRegistryImpl.INSTANCE.replace(Biomes.END_HIGHLANDS, moddedBiome, weight);
    }

    static void addMidlandBiome(ResourceKey<Biome> moddedBiome, int weight) {
        EndBiomeRegistryImpl.INSTANCE.replace(Biomes.END_MIDLANDS, moddedBiome, weight);
    }

    static void addBarrensBiome(ResourceKey<Biome> moddedBiome, int weight) {
        EndBiomeRegistryImpl.INSTANCE.replace(Biomes.END_BARRENS, moddedBiome, weight);
    }

    static void addSmallIslandsBiome(ResourceKey<Biome> moddedBiome, int weight) {
        EndBiomeRegistryImpl.INSTANCE.replace(Biomes.SMALL_END_ISLANDS, moddedBiome, weight);
    }

    void replace(ResourceKey<Biome> originalBiome, ResourceKey<Biome> moddedBiome, int weight);
}
