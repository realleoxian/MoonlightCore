package de.leoxian.moonlightcore.levelgen.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.function.BiConsumer;

public interface BiomeProvider {

    void bootstrap(BiConsumer<ResourceKey<Biome>, Climate.ParameterPoint> output);

    default void addBiome(ResourceKey<Biome> key, Climate.ParameterPoint parameterPoint, BiConsumer<ResourceKey<Biome>, Climate.ParameterPoint> output) {
        output.accept(key, parameterPoint);
    }

}
