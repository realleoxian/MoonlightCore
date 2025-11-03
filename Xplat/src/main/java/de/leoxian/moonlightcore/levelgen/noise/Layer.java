package de.leoxian.moonlightcore.levelgen.noise;

import com.google.common.collect.ImmutableList;
import de.leoxian.moonlightcore.levelgen.biome.BiomeProvider;
import de.leoxian.moonlightcore.levelgen.biome.BiomeProviderRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;

import java.util.List;

public class Layer extends WeightedRandomLayer<WeightedEntry.Wrapper<BiomeProvider>> {

    private static List<WeightedEntry.Wrapper<BiomeProvider>> buildEntries(BiomeProviderRegistry.Dimension dimension) {
        ImmutableList.Builder<WeightedEntry.Wrapper<BiomeProvider>> entries = ImmutableList.builder();

        BiomeProviderRegistry registry = BiomeProviderRegistry.get(dimension);

        for(ResourceLocation key : registry.keys()) {
            BiomeProvider provider = registry.get(key);
            if(provider == null) {
                continue;
            }

            int weight = registry.getWeight(provider);
            entries.add(WeightedEntry.wrap(provider, weight));
        }

        return entries.build();
    }

    private final BiomeProviderRegistry registry;

    Layer(BiomeProviderRegistry.Dimension dimension) {
        super(buildEntries(dimension));
        this.registry = BiomeProviderRegistry.get(dimension);
    }

    @Override
    protected int getEntryIndex(WeightedEntry.Wrapper<BiomeProvider> entry) {
        return this.registry.getId(entry.getData());
    }

    @Override
    protected int getDefaultIndex() {
        return this.registry.getId(this.registry.byId(0));
    }

}
