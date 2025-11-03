package de.leoxian.moonlightcore.levelgen.noise;

import de.leoxian.moonlightcore.core.ModConfig;
import de.leoxian.moonlightcore.levelgen.biome.BiomeProviderRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.function.LongFunction;

public class LayeredNoiseUtil {

    public static Area uniqueness(BiomeProviderRegistry.Dimension dimension, long seed) {
        return createUniqueness(dimension, seed, createUniquenessLayer(dimension));
    }

    public static Area biomeUniqueness(RegistryAccess registryAccess, List<WeightedEntry.Wrapper<ResourceKey<Biome>>> entries, long seed) {
        return createZoomedArea(new BiomeLayer(registryAccess, entries), ModConfig.COMMON.endBiomeZooms(), seed);
    }

    private static Area createUniqueness(BiomeProviderRegistry.Dimension dimension, long seed, Layer layer) {
        int zooms = switch (dimension) {
            case OVERWORLD -> ModConfig.COMMON.overworldBiomeZooms();
            case NETHER -> ModConfig.COMMON.netherBiomeZooms();
        };

        return createZoomedArea(layer, zooms, seed);
    }

    private static Layer createUniquenessLayer(BiomeProviderRegistry.Dimension dimension) {
        return new Layer(dimension);
    }

    private static Area createZoomedArea(AreaTransformer0 transformer, int zooms, long seed) {
        LongFunction<AreaContext> contextFactory = (modifier) -> new AreaContext(25, seed, modifier);
        AreaFactory factory = transformer.run(contextFactory.apply(1L));
        factory = ZoomLayer.FUZZY.run(contextFactory.apply(2000L), factory);
        factory = zoom(2001L, ZoomLayer.NORMAL, factory, 3, contextFactory);
        factory = zoom(1001L, ZoomLayer.NORMAL, factory, zooms, contextFactory);

        return factory.make();
    }

    private static AreaFactory zoom(long modifier, AreaTransformer1 transformer, AreaFactory initialAreaFactory, int times, LongFunction<AreaContext> contextFactory) {
        AreaFactory factory = initialAreaFactory;

        for(int i = 0; i < times; i++) {
            factory = transformer.run(contextFactory.apply(modifier + (long) i), factory);
        }

        return factory;
    }

}
