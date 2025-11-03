package de.leoxian.moonlightcore.mixin;

import com.google.common.collect.ImmutableList;
import de.leoxian.moonlightcore.levelgen.BiomeSourceExtension;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.*;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mixin(BiomeSource.class)
public abstract class BiomeSourceMixin implements BiomeResolver, BiomeSourceExtension {

    @Mutable
    @Shadow
    @Final
    private Supplier<Set<Holder<Biome>>> possibleBiomes;
    @Unique
    private boolean mlcore_hasMerged = false;

    @Override
    public void mlcore_mergeBiomes(Collection<Holder<Biome>> moddedBiomes) {
        if(this.mlcore_hasMerged) {
            return;
        }
        this.mlcore_hasMerged = true;

        ImmutableList.Builder<Holder<Biome>> mergedBiomesBuilder = ImmutableList.builder();
        mergedBiomesBuilder.addAll(this.possibleBiomes.get());
        mergedBiomesBuilder.addAll(moddedBiomes);

        this.possibleBiomes = () -> new ObjectLinkedOpenHashSet<>(mergedBiomesBuilder.build().stream().distinct().collect(ImmutableList.toImmutableList()));
    }

}
