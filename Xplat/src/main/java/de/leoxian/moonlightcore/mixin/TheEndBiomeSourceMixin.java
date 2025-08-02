package de.leoxian.moonlightcore.mixin;

import de.leoxian.moonlightcore.api.world.biome.EndBiomeRegistry;
import de.leoxian.moonlightcore.world.TheEndBiomeSourceExtension;
import de.leoxian.moonlightcore.world.biome.EndBiomeRegistryImpl;
import de.leoxian.moonlightcore.world.biome.layer.Area;
import de.leoxian.moonlightcore.world.biome.layer.AreaWeightedPicker;
import de.leoxian.moonlightcore.world.biome.layer.LayeredNoiseUtils;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Mixin(TheEndBiomeSource.class)
public class TheEndBiomeSourceMixin implements TheEndBiomeSourceExtension {

    @Shadow @Final private Holder<Biome> end;
    @Unique
    private boolean mlcore_initialized = false;

    @Unique
    private Registry<Biome> mlcore_biomeRegistry = null;

    @Unique
    private Set<Holder<Biome>> mlcore_allBiomes = null;

    @Unique
    private Area mlcore_highlandsArea = null;
    @Unique
    private Area mlcore_midlandsArea = null;
    @Unique
    private Area mlcore_barrensArea = null;
    @Unique
    private Area mlcore_smallIslandsArea = null;

    @Override
    public void mlcore_initialize(RegistryAccess registryAccess, long seed) {
        if(this.mlcore_initialized) return;
        this.mlcore_biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);

        this.mlcore_allBiomes = new HashSet<>();
        this.mlcore_allBiomes.addAll(EndBiomeRegistryImpl.INSTANCE.getEntriesOf(Biomes.END_HIGHLANDS).stream().map(AreaWeightedPicker.WeightedWrapper::data).map(this.mlcore_biomeRegistry::getHolderOrThrow).toList());
        this.mlcore_allBiomes.addAll(EndBiomeRegistryImpl.INSTANCE.getEntriesOf(Biomes.END_MIDLANDS).stream().map(AreaWeightedPicker.WeightedWrapper::data).map(this.mlcore_biomeRegistry::getHolderOrThrow).toList());
        this.mlcore_allBiomes.addAll(EndBiomeRegistryImpl.INSTANCE.getEntriesOf(Biomes.END_BARRENS).stream().map(AreaWeightedPicker.WeightedWrapper::data).map(this.mlcore_biomeRegistry::getHolderOrThrow).toList());
        this.mlcore_allBiomes.addAll(EndBiomeRegistryImpl.INSTANCE.getEntriesOf(Biomes.SMALL_END_ISLANDS).stream().map(AreaWeightedPicker.WeightedWrapper::data).map(this.mlcore_biomeRegistry::getHolderOrThrow).toList());

        this.mlcore_highlandsArea = LayeredNoiseUtils.endUniqueness(registryAccess, seed, EndBiomeRegistryImpl.INSTANCE.getEntriesOf(Biomes.END_HIGHLANDS));
        this.mlcore_midlandsArea = LayeredNoiseUtils.endUniqueness(registryAccess, seed, EndBiomeRegistryImpl.INSTANCE.getEntriesOf(Biomes.END_MIDLANDS));
        this.mlcore_barrensArea = LayeredNoiseUtils.endUniqueness(registryAccess, seed, EndBiomeRegistryImpl.INSTANCE.getEntriesOf(Biomes.END_BARRENS));
        this.mlcore_smallIslandsArea = LayeredNoiseUtils.endUniqueness(registryAccess, seed, EndBiomeRegistryImpl.INSTANCE.getEntriesOf(Biomes.SMALL_END_ISLANDS));

        this.mlcore_initialized = true;
    }

    @Inject(method = "collectPossibleBiomes", at = @At("RETURN"), cancellable = true)
    public void mlcore_collectPossibleBiomes(CallbackInfoReturnable<Stream<Holder<Biome>>> cir) {
        if(!this.mlcore_initialized) return;

        cir.setReturnValue(Stream.concat(this.mlcore_allBiomes.stream(), cir.getReturnValue()));
    }

    @Inject(method = "getNoiseBiome", at = @At("HEAD"), cancellable = true)
    public void mlcore_getNoiseBiome(int x, int y, int z, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        int blockX = QuartPos.toBlock(x);
        int blockY = QuartPos.toBlock(y);
        int blockZ = QuartPos.toBlock(z);

        int sectionX = SectionPos.blockToSectionCoord(blockX);
        int sectionZ = SectionPos.blockToSectionCoord(blockZ);

        if((long)sectionX * (long)sectionX + (long)sectionZ * (long)sectionZ <= 4096L) {
            cir.setReturnValue(this.end);
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
