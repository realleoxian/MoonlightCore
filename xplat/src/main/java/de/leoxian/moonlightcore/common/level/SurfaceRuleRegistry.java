package de.leoxian.moonlightcore.common.level;

import de.leoxian.moonlightcore.internal.common.level.SurfaceRuleRegistryImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;

public interface SurfaceRuleRegistry {
    static SurfaceRuleRegistry get(ResourceKey<LevelStem> key) {
        return SurfaceRuleRegistryImpl.get(key);
    }

    void add(SurfaceRules.RuleSource ruleSource);
}
