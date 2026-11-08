package de.leoxian.moonlightcore.common.level.biome.modifier;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

@FunctionalInterface
public interface BiomeSelector {
    boolean test(Holder<Biome> holder);
}
