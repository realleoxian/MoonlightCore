package de.leoxian.moonlightcore.world.biome;

import de.leoxian.moonlightcore.api.world.biome.BiomeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

import java.util.function.BiConsumer;

final class VanillaNetherBiomeProvider implements BiomeProvider {

    @Override
    public void bootstrap(BiConsumer<ResourceKey<Biome>, Climate.ParameterPoint> output) {
        this.addBiome(Biomes.NETHER_WASTES, Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), output);
        this.addBiome(Biomes.SOUL_SAND_VALLEY, Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), output);
        this.addBiome(Biomes.CRIMSON_FOREST, Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F), output);
        this.addBiome(Biomes.WARPED_FOREST, Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F), output);
        this.addBiome(Biomes.BASALT_DELTAS, Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F), output);
    }

}
