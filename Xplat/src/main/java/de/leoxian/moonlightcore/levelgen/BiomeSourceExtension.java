package de.leoxian.moonlightcore.levelgen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import java.util.Collection;

public interface BiomeSourceExtension {

    void mlcore_mergeBiomes(Collection<Holder<Biome>> moddedBiomes);

}
