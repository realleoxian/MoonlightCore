package de.leoxian.moonlightcore.world;

import de.leoxian.moonlightcore.api.world.biome.BiomeProviderRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Climate;

public interface ParameterListExtension<T> {

    void mlcore_initialize(RegistryAccess registryAccess, BiomeProviderRegistry.Dimension dimension, long seed);

    T mlcore_find(Climate.TargetPoint target, int x, int y, int z);

}
