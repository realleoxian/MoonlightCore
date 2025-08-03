package de.leowgc.moonlightcore.mixin;

import com.google.common.collect.ImmutableList;
import de.leowgc.moonlightcore.world.BiomeSourceExtension;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import org.spongepowered.asm.mixin.*;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeSource.class)
public final class BiomeSourceMixin implements BiomeSourceExtension {

    @Shadow @Final @Mutable
    private Supplier<Set<Holder<Biome>>> possibleBiomes;
    @Unique
    private boolean mlcore_merged = false;

    @Override
    public void mlcore_mergeBiomes(Collection<Holder<Biome>> moddedBiomes) {
        if(this.mlcore_merged) return;

        ImmutableList.Builder<Holder<Biome>> possibleBiomes = ImmutableList.builder();
        possibleBiomes.addAll(this.possibleBiomes.get());
        possibleBiomes.addAll(moddedBiomes);

        this.possibleBiomes = () -> new ObjectLinkedOpenHashSet<>(possibleBiomes.build());
        this.mlcore_merged = true;
    }
}
