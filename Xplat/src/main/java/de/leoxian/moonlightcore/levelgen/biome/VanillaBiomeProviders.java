package de.leoxian.moonlightcore.levelgen.biome;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class VanillaBiomeProviders {
    static final BiomeProvider OVERWORLD = (output) -> new OverworldBiomeBuilder().addBiomes(pair -> output.accept(pair.getSecond(), pair.getFirst()));

    static final BiomeProvider NETHER = (output) -> {
        output.accept(Biomes.NETHER_WASTES, Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        output.accept(Biomes.SOUL_SAND_VALLEY, Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        output.accept(Biomes.CRIMSON_FOREST, Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        output.accept(Biomes.WARPED_FOREST, Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F));
        output.accept(Biomes.BASALT_DELTAS, Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F));
    };

    private VanillaBiomeProviders() {}
}
