package de.leoxian.moonlightcore.world.biome.layer;

import de.leoxian.moonlightcore.api.world.biome.BiomeProviderRegistry;
import de.leoxian.moonlightcore.core.MoonlightCoreConfiguration;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;
import java.util.function.LongFunction;

public final class LayeredNoiseUtils {

    public static Area uniqueness(BiomeProviderRegistry.Dimension dimension, long seed) {
        int zooms = dimension == BiomeProviderRegistry.Dimension.OVERWORLD ? MoonlightCoreConfiguration.COMMON.overworldBiomeZooms() : MoonlightCoreConfiguration.COMMON.netherBiomeZooms();
        return createZoomedArea(seed, zooms, new Layer(dimension));
    }

    public static Area endUniqueness(RegistryAccess registryAccess, long seed, Collection<AreaWeightedPicker.WeightedWrapper<ResourceKey<Biome>>> entries) {
        return createZoomedArea(seed, MoonlightCoreConfiguration.COMMON.endBiomeZooms(), new BiomeLayer(registryAccess, entries));
    }

    private static Area createZoomedArea(long seed, int zooms, AreaTransformer0 transformer) {
        LongFunction<AreaContext> contextFactory = (modifier) -> new AreaContext(seed, modifier, 25);
        AreaFactory factory = transformer.run(contextFactory.apply(1));
        factory = ZoomLayer.FUZZY.run(contextFactory.apply(2000L), factory);
        factory = zoom(2001L, factory, 3, contextFactory);
        factory = zoom(1001L, factory, zooms, contextFactory);

        return factory.make();
    }

    private static AreaFactory zoom(long seedModifier, AreaFactory initialFactory, int times, LongFunction<AreaContext> contextFactory) {
        AreaFactory factory = initialFactory;

        for(int i = 0; i < times; i++) {
            factory = ZoomLayer.NORMAL.run(contextFactory.apply(seedModifier * (long) i), factory);
        }

        return factory;
    }

    private LayeredNoiseUtils() {}
}
