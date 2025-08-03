package de.leowgc.moonlightcore.world.biome.layer;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;

public class BiomeLayer extends WeightedLayer<ResourceKey<Biome>> {
    private final Registry<Biome> biomeRegistry;

    BiomeLayer(RegistryAccess registryAccess, Collection<AreaWeightedPicker.WeightedWrapper<ResourceKey<Biome>>> weightedWrappers) {
        super(weightedWrappers);
        this.biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);
    }

    @Override
    public int getEntryIndex(ResourceKey<Biome> data) {
        return this.biomeRegistry.getId(this.biomeRegistry.getOrThrow(data));
    }
}
