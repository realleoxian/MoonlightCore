package de.leowgc.moonlightcore.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.leowgc.moonlightcore.api.world.biome.BiomeProvider;
import de.leowgc.moonlightcore.api.world.biome.BiomeProviderRegistry;
import de.leowgc.moonlightcore.world.ParameterListExtension;
import de.leowgc.moonlightcore.world.biome.BiomeProviderRegistryImpl;
import de.leowgc.moonlightcore.world.biome.layer.Area;
import de.leowgc.moonlightcore.world.biome.layer.LayeredNoiseUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(Climate.ParameterList.class)
public abstract class ParameterListMixin<T> implements ParameterListExtension<T> {

    @Shadow @Final
    private List<Pair<Climate.ParameterPoint, T>> values;

    @Shadow
    public abstract T findValue(Climate.TargetPoint targetPoint);

    @Unique
    private boolean mlcore_initialized = false;

    @Unique
    private Climate.RTree<Holder<Biome>>[] mlcore_trees = null;
    @Unique
    private Area mlcore_uniqueness = null;

    @Override
    @SuppressWarnings("unchecked")
    public void mlcore_initialize(RegistryAccess registryAccess, BiomeProviderRegistry.Dimension dimension, long seed) {
        if(this.mlcore_initialized) return;

        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);

        this.mlcore_uniqueness = LayeredNoiseUtils.uniqueness(dimension, seed);
        this.mlcore_trees = new Climate.RTree[BiomeProviderRegistryImpl.get(dimension).getProviderCount()];

        for(BiomeProvider provider : BiomeProviderRegistryImpl.get(dimension).getEntries().stream().map(BiomeProviderRegistryImpl.ProviderEntry::provider).toList()) {
            int providerIndex = BiomeProviderRegistryImpl.get(dimension).getProviderId(provider);

            if(providerIndex == 0) {
                this.mlcore_trees[0] = (Climate.RTree<Holder<Biome>>) Climate.RTree.create(this.values);
            } else {
                ImmutableList.Builder<Pair<Climate.ParameterPoint, Holder<Biome>>> builder = ImmutableList.builder();

                provider.bootstrap((key, point) -> {
                    if(biomeRegistry.getHolder(key).isPresent()) {
                        builder.add(Pair.of(point, biomeRegistry.getHolderOrThrow(key)));
                    }
                });
                ImmutableList<Pair<Climate.ParameterPoint, Holder<Biome>>> values = builder.build();

                if(!values.isEmpty()) {
                    this.mlcore_trees[providerIndex] = Climate.RTree.create(values);
                }
            }
        }

        this.mlcore_initialized = true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T mlcore_find(Climate.TargetPoint target, int x, int y, int z) {
        if(!this.mlcore_initialized) {
            return this.findValue(target);
        }

        int uniqueness = this.mlcore_uniqueness.get(x, z);
        Holder<Biome> biomeHolder = this.mlcore_trees[uniqueness].search(target, Climate.RTree.Node::distance);

        return (T) biomeHolder;
    }
}
