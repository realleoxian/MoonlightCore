package de.leoxian.moonlightcore.world.biome.layer;

import de.leoxian.moonlightcore.api.world.biome.BiomeProvider;
import de.leoxian.moonlightcore.api.world.biome.BiomeProviderRegistry;
import de.leoxian.moonlightcore.world.biome.BiomeProviderRegistryImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Layer extends WeightedLayer<BiomeProvider> {

    private static Collection<AreaWeightedPicker.WeightedWrapper<BiomeProvider>> buildEntries(BiomeProviderRegistry.Dimension dimension) {
        List<AreaWeightedPicker.WeightedWrapper<BiomeProvider>> providers = new ArrayList<>();
        BiomeProviderRegistryImpl.get(dimension).getEntries().forEach((entry) -> providers.add(new AreaWeightedPicker.WeightedWrapper<>(entry.provider(), entry.weight())));

        return providers;
    }

    private final BiomeProviderRegistry.Dimension dimension;

    Layer(BiomeProviderRegistry.Dimension dimension) {
        super(buildEntries(dimension));
        this.dimension = dimension;
    }

    @Override
    public int getEntryIndex(BiomeProvider data) {
        return BiomeProviderRegistryImpl.get(this.dimension).getProviderId(data);
    }

}
