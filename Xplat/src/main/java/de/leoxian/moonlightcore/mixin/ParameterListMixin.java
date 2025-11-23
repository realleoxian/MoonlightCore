package de.leoxian.moonlightcore.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import de.leoxian.moonlightcore.levelgen.ParameterListExtension;
import de.leoxian.moonlightcore.levelgen.biome.BiomeProvider;
import de.leoxian.moonlightcore.levelgen.biome.BiomeProviderRegistry;
import de.leoxian.moonlightcore.levelgen.noise.Area;
import de.leoxian.moonlightcore.levelgen.noise.LayeredNoiseUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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
    private Climate.RTree<Holder<Biome>>[] mlcore_tress = null;

    @Unique
    private Area mlcore_uniqueness = null;

    @Override
    @SuppressWarnings("unchecked")
    public void mlcore_initialize(RegistryAccess registryAccess, BiomeProviderRegistry.Dimension dimension, long seed) {
        if(this.mlcore_initialized) {
            return;
        }
        this.mlcore_initialized = true;

        BiomeProviderRegistry registry = BiomeProviderRegistry.get(dimension);
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);

        this.mlcore_uniqueness = LayeredNoiseUtil.uniqueness(dimension, seed);
        this.mlcore_tress = new Climate.RTree[registry.keys().size()];

        for(ResourceLocation key : registry.keys()) {
            BiomeProvider provider = registry.get(key);
            if(provider == null) {
                continue;
            }

            int id = registry.getId(provider);

            if(id == 0) {
                this.mlcore_tress[0] = (Climate.RTree<Holder<Biome>>) Climate.RTree.create(this.values);
            } else {
                ImmutableList.Builder<Pair<Climate.ParameterPoint, Holder<Biome>>> builder = ImmutableList.builder();

                provider.bootstrap((biomeKey, point) -> {
                    if(biomeRegistry.containsKey(biomeKey)) {
                       builder.add(Pair.of(point, biomeRegistry.getHolderOrThrow(biomeKey)));
                    }
                });

                ImmutableList<Pair<Climate.ParameterPoint, Holder<Biome>>> values = builder.build();

                if(!values.isEmpty()) {
                    this.mlcore_tress[id] = Climate.RTree.create(values);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T mlcore_find(Climate.TargetPoint target, int x, int y, int z) {
        if(!this.mlcore_initialized) {
            return this.findValue(target);
        }

        int uniqueness = this.mlcore_uniqueness.get(x, z);
        Holder<Biome> holder = (Holder<Biome>) this.mlcore_tress[uniqueness].search(target, Climate.RTree.Node::distance);

        return (T) holder;
    }
}
