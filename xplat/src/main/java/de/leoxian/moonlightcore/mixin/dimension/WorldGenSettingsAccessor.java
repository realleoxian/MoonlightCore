package de.leoxian.moonlightcore.mixin.dimension;

import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldGenSettings.class)
public interface WorldGenSettingsAccessor {
    @Accessor
    void setDimensions(WorldDimensions dimensions);
}
