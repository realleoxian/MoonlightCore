package de.leoxian.moonlightcore.mixin;

import com.google.common.collect.ImmutableSet;
import de.leoxian.moonlightcore.levelgen.TheEndBiomeSourceExtension;
import de.leoxian.moonlightcore.levelgen.biome.EndBiomeRegistry;
import de.leoxian.moonlightcore.levelgen.noise.Area;
import de.leoxian.moonlightcore.levelgen.noise.LayeredNoiseUtil;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(TheEndBiomeSource.class)
public class TheEndBiomeSourceMixin implements TheEndBiomeSourceExtension {
    @Unique
    private boolean mlcore_initialized = false;

    @Unique
    private Registry<Biome> mlcore_biomeRegistry = null;
    @Unique
    private Set<Holder<Biome>> mlcore_allBiomes = null;

    @Unique
    private Area mlcore_mainIslandArea = null;
    @Unique
    private Area mlcore_highlandsArea = null;
    @Unique
    private Area mlcore_midlandsArea = null;
    @Unique
    private Area mlcore_smallIslandsArea = null;
    @Unique
    private Area mlcore_barrensArea = null;

    @Override
    public void mlcore_initialize(RegistryAccess registryAccess, long seed) {
        if(this.mlcore_initialized) {
            return;
        }
        this.mlcore_initialized = true;
        this.mlcore_biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);

        this.mlcore_allBiomes = ImmutableSet.copyOf(EndBiomeRegistry.getAllBiomes().stream().map(this.mlcore_biomeRegistry::getHolderOrThrow).collect(Collectors.toSet()));

        this.mlcore_mainIslandArea = LayeredNoiseUtil.biomeUniqueness(registryAccess, EndBiomeRegistry.getBiomeReplacements(Biomes.THE_END), seed);
        this.mlcore_highlandsArea = LayeredNoiseUtil.biomeUniqueness(registryAccess, EndBiomeRegistry.getBiomeReplacements(Biomes.END_HIGHLANDS), seed);
        this.mlcore_midlandsArea = LayeredNoiseUtil.biomeUniqueness(registryAccess, EndBiomeRegistry.getBiomeReplacements(Biomes.END_MIDLANDS), seed);
        this.mlcore_smallIslandsArea = LayeredNoiseUtil.biomeUniqueness(registryAccess, EndBiomeRegistry.getBiomeReplacements(Biomes.SMALL_END_ISLANDS), seed);
        this.mlcore_barrensArea = LayeredNoiseUtil.biomeUniqueness(registryAccess, EndBiomeRegistry.getBiomeReplacements(Biomes.END_BARRENS), seed);
    }

    @Inject(method = "collectPossibleBiomes", at = @At("HEAD"), cancellable = true)
    public void mlcore_collectPossibleBiomes(CallbackInfoReturnable<Stream<Holder<Biome>>> cir) {
        if(!this.mlcore_initialized) {
            return;
        }

        cir.setReturnValue(this.mlcore_allBiomes.stream());
    }

    @Inject(method = "getNoiseBiome", at = @At("HEAD"), cancellable = true)
    public void mlcore_getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        int blockX = QuartPos.toBlock(x);
        int blockY = QuartPos.toBlock(y);
        int blockZ = QuartPos.toBlock(z);

        int sectionX = SectionPos.blockToSectionCoord(blockX);
        int sectionZ = SectionPos.blockToSectionCoord(blockZ);

        if((long) sectionX * (long) sectionX + (long) sectionZ * (long) sectionZ <= 4096L) {
            cir.setReturnValue(this.mlcore_getHolder(this.mlcore_mainIslandArea.get(x, z)));
        } else {
            int i = (SectionPos.blockToSectionCoord(blockX) * 2 + 1) * 8;
            int j = (SectionPos.blockToSectionCoord(blockZ) * 2 + 1) * 8;

            double erosion = sampler.erosion().compute(new DensityFunction.SinglePointContext(i, blockY, j));

            if(erosion >= 0.25) {
                cir.setReturnValue(this.mlcore_getHolder(this.mlcore_highlandsArea.get(x, z)));
            } else if(erosion >= -0.0625) {
                cir.setReturnValue(this.mlcore_getHolder(this.mlcore_midlandsArea.get(x, z)));
            } else {
                if(erosion < -0.21875) {
                    cir.setReturnValue(this.mlcore_getHolder(this.mlcore_smallIslandsArea.get(x, z)));
                } else {
                    cir.setReturnValue(this.mlcore_getHolder(this.mlcore_barrensArea.get(x, z)));
                }
            }
        }
    }

    @Unique
    private Holder<Biome> mlcore_getHolder(int id) {
        return this.mlcore_biomeRegistry.getHolder(id).orElseThrow();
    }
}
