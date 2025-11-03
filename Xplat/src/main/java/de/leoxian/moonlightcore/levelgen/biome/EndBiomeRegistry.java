package de.leoxian.moonlightcore.levelgen.biome;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Collectors;

public class EndBiomeRegistry {
    private static final Set<ResourceKey<Biome>> VANILLA_BIOMES = Set.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS);
    private static final Map<ResourceKey<Biome>, BiomeReplacement> REPLACEMENTS = new HashMap<>();

    static {
        for(ResourceKey<Biome> vanillaBiome : VANILLA_BIOMES) {
            BiomeReplacement replacement = new BiomeReplacement();
            replacement.addReplacement(vanillaBiome, 1000);

            REPLACEMENTS.put(vanillaBiome, replacement);
        }
    }

    public static void replace(ResourceKey<Biome> original, ResourceKey<Biome> replacement, int weight) {
        Objects.requireNonNull(original, "Original biome key may not be null");
        Objects.requireNonNull(replacement, "Replacement biome key may not be null");

        REPLACEMENTS.computeIfAbsent(original, k -> new BiomeReplacement()).addReplacement(replacement, weight);
    }

    public static void replaceMainIsland(ResourceKey<Biome> replacement, int weight) {
        replace(Biomes.THE_END, replacement, weight);
    }

    public static void replaceHighlandBiome(ResourceKey<Biome> replacement, int weight) {
        replace(Biomes.END_HIGHLANDS, replacement, weight);
    }

    public static void replaceMidlandBiome(ResourceKey<Biome> replacement, int weight) {
        replace(Biomes.END_MIDLANDS, replacement, weight);
    }

    public static void replaceSmallIslandBiome(ResourceKey<Biome> replacement, int weight) {
        replace(Biomes.SMALL_END_ISLANDS, replacement, weight);
    }

    public static void replaceBarrensBiome(ResourceKey<Biome> replacement, int weight) {
        replace(Biomes.END_BARRENS, replacement, weight);
    }

    @ApiStatus.Internal
    public static List<WeightedEntry.Wrapper<ResourceKey<Biome>>> getBiomeReplacements(ResourceKey<Biome> original) {
        ImmutableList.Builder<WeightedEntry.Wrapper<ResourceKey<Biome>>> biomes = ImmutableList.builder();

        for(WeightedEntry.Wrapper<ResourceKey<Biome>> replacement : REPLACEMENTS.get(original).entries()) {
            biomes.add(replacement);
        }

        return biomes.build();
    }

    @ApiStatus.Internal
    public static Set<ResourceKey<Biome>> getAllBiomes() {
        ImmutableSet.Builder<ResourceKey<Biome>> builder = ImmutableSet.builder();

        for(var replacement : REPLACEMENTS.values()) {
            builder.addAll(replacement.entries().stream().map(WeightedEntry.Wrapper::getData).collect(Collectors.toSet()));
        }

        return builder.build();
    }

    private EndBiomeRegistry() {}

    @ApiStatus.Internal
    private static class BiomeReplacement {
        private final Set<WeightedEntry.Wrapper<ResourceKey<Biome>>> entries = new HashSet<>();

        void addReplacement(ResourceKey<Biome> key, int weight) {
            Preconditions.checkArgument(weight >= 0, "Weight may not be null");
            this.entries.add(WeightedEntry.wrap(key, weight));
        }

        public Set<WeightedEntry.Wrapper<ResourceKey<Biome>>> entries() {
            return ImmutableSet.copyOf(this.entries);
        }

        private BiomeReplacement() {}
    }
}
