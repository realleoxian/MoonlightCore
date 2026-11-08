package de.leoxian.moonlightcore.internal.common.level;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

public interface NoiseGeneratorSettingsExtension {
    void moonlightcore$setDimensionKey(ResourceKey<LevelStem> key);
}
