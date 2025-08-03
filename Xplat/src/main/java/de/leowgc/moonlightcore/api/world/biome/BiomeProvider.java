package de.leowgc.moonlightcore.api.world.biome;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.function.BiConsumer;

public interface BiomeProvider {

    void bootstrap(BiConsumer<ResourceKey<Biome>, Climate.ParameterPoint> output);

    default void addBiome(ResourceKey<Biome> biomeKey, Climate.ParameterPoint parameterPoint, BiConsumer<ResourceKey<Biome>, Climate.ParameterPoint> output) {
        output.accept(biomeKey, parameterPoint);
    }

}
