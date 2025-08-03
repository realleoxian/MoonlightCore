package de.leowgc.moonlightcore.world.biome;

import de.leowgc.moonlightcore.api.world.biome.BiomeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;

import java.util.function.BiConsumer;

final class VanillaOverworldBiomeProvider implements BiomeProvider {

    @Override
    public void bootstrap(BiConsumer<ResourceKey<Biome>, Climate.ParameterPoint> output) {
        new OverworldBiomeBuilder().addBiomes((pair) -> output.accept(pair.getSecond(), pair.getFirst()));
    }

}
