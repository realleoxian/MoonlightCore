package de.leoxian.moonlightcore.common.level.biome.modifier;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface BiomeModifier {
    void modifyBiome(Holder<Biome> holder, BiomeModificationContext context);
}
