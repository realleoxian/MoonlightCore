package de.leowgc.moonlightcore.world.biome;

import com.google.common.collect.ImmutableList;
import de.leowgc.moonlightcore.api.world.biome.BiomeProvider;
import de.leowgc.moonlightcore.api.world.biome.BiomeProviderRegistry;
import de.leowgc.moonlightcore.api.world.surface.SurfaceRuleRegistry;
import de.leowgc.moonlightcore.mixin.MultiNoiseBiomeSourceAccessor;
import de.leowgc.moonlightcore.world.BiomeSourceExtension;
import de.leowgc.moonlightcore.world.NoiseGeneratorSettingsExtension;
import de.leowgc.moonlightcore.world.ParameterListExtension;
import de.leowgc.moonlightcore.world.TheEndBiomeSourceExtension;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public final class BiomeApi {

    public static void setupBiomeApi(MinecraftServer server) {
        RegistryAccess registryAccess = server.registryAccess();
        Registry<LevelStem> stemRegistry = registryAccess.registryOrThrow(Registries.LEVEL_STEM);

        long seed = server.getWorldData().worldGenOptions().seed();

        for(var entry : stemRegistry.entrySet()) {
            ResourceKey<LevelStem> stemKey = entry.getKey();
            LevelStem stem = entry.getValue();

            setupWorldGeneration(registryAccess, stemKey, stem.generator(), stem.generator().getBiomeSource(), seed);
        }
    }

    @SuppressWarnings("unchecked")
    private static void setupWorldGeneration(RegistryAccess registryAccess, ResourceKey<LevelStem> levelStemKey, ChunkGenerator chunkGenerator, BiomeSource biomeSource, long seed) {
        if(!(chunkGenerator instanceof NoiseBasedChunkGenerator noiseBasedChunkGenerator)) {
            return;
        }

        NoiseGeneratorSettings generatorSettings = noiseBasedChunkGenerator.generatorSettings().value();

        if(biomeSource instanceof TheEndBiomeSource) {
            ((TheEndBiomeSourceExtension) biomeSource).mlcore_initialize(registryAccess, seed);
            ((NoiseGeneratorSettingsExtension) (Object) generatorSettings).mlcore_setDimension(SurfaceRuleRegistry.Dimension.THE_END);
            return;
        } else if(!(biomeSource instanceof MultiNoiseBiomeSource)) {
            return;
        }

        BiomeProviderRegistry.Dimension biomeDimension = null;
        SurfaceRuleRegistry.Dimension surfaceDimension = null;
        if(levelStemKey == LevelStem.OVERWORLD) {
            biomeDimension = BiomeProviderRegistry.Dimension.OVERWORLD;
            surfaceDimension = SurfaceRuleRegistry.Dimension.OVERWORLD;
        } else if(levelStemKey == LevelStem.NETHER) {
            biomeDimension = BiomeProviderRegistry.Dimension.NETHER;
            surfaceDimension = SurfaceRuleRegistry.Dimension.NETHER;
        }

        if(biomeDimension == null) {
            return;
        }

        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);

        MultiNoiseBiomeSource multiNoiseBiomeSource = (MultiNoiseBiomeSource) biomeSource;
        Climate.ParameterList<Holder<Biome>> parameters = ((MultiNoiseBiomeSourceAccessor) multiNoiseBiomeSource).mlcore_parameters();

        ((NoiseGeneratorSettingsExtension) (Object) generatorSettings).mlcore_setDimension(surfaceDimension);
        ((ParameterListExtension<Holder<Biome>>) parameters).mlcore_initialize(registryAccess, biomeDimension, seed);

        ImmutableList.Builder<Holder<Biome>> moddedBiomes = ImmutableList.builder();

        for(BiomeProvider provider : BiomeProviderRegistryImpl.get(biomeDimension).getEntries().stream().map(BiomeProviderRegistryImpl.ProviderEntry::provider).toList()) {
            provider.bootstrap((key, point) -> {
                if(biomeRegistry.getHolder(key).isPresent()) {
                    moddedBiomes.add(biomeRegistry.getHolderOrThrow(key));
                }
            });
        }

        ((BiomeSourceExtension) multiNoiseBiomeSource).mlcore_mergeBiomes(moddedBiomes.build());
    }

    private BiomeApi() {}
}
