package de.leowgc.moonlightcore.world.biome;

import com.google.common.collect.ImmutableSet;
import de.leowgc.moonlightcore.api.world.biome.EndBiomeRegistry;
import de.leowgc.moonlightcore.world.biome.layer.AreaWeightedPicker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class EndBiomeRegistryImpl implements EndBiomeRegistry {
    public static final EndBiomeRegistryImpl INSTANCE = new EndBiomeRegistryImpl();

    private final Map<ResourceKey<Biome>, Set<AreaWeightedPicker.WeightedWrapper<ResourceKey<Biome>>>> replacedBiomes = new HashMap<>();

    private EndBiomeRegistryImpl() {
        this.replacedBiomes.computeIfAbsent(Biomes.END_HIGHLANDS, k -> new HashSet<>()).add(new AreaWeightedPicker.WeightedWrapper<>(Biomes.END_HIGHLANDS, 1000));
        this.replacedBiomes.computeIfAbsent(Biomes.END_MIDLANDS, k -> new HashSet<>()).add(new AreaWeightedPicker.WeightedWrapper<>(Biomes.END_MIDLANDS, 1000));
        this.replacedBiomes.computeIfAbsent(Biomes.END_BARRENS, k -> new HashSet<>()).add(new AreaWeightedPicker.WeightedWrapper<>(Biomes.END_BARRENS, 1000));
        this.replacedBiomes.computeIfAbsent(Biomes.SMALL_END_ISLANDS, k -> new HashSet<>()).add(new AreaWeightedPicker.WeightedWrapper<>(Biomes.SMALL_END_ISLANDS, 1000));
    }

    @Override
    public void replace(ResourceKey<Biome> originalBiome, ResourceKey<Biome> moddedBiome, int weight) {
        this.replacedBiomes.computeIfAbsent(originalBiome, k -> new HashSet<>()).add(new AreaWeightedPicker.WeightedWrapper<>(moddedBiome, weight));
    }

    @ApiStatus.Internal
    public Set<AreaWeightedPicker.WeightedWrapper<ResourceKey<Biome>>> getEntriesOf(ResourceKey<Biome> originalBiome) {
        return ImmutableSet.copyOf(this.replacedBiomes.get(originalBiome));
    }
}
