package de.leoxian.moonlightcore.api.world.surface;

import de.leoxian.moonlightcore.world.surface.SurfaceRuleRegistryImpl;
import net.minecraft.world.level.levelgen.SurfaceRules;

public interface SurfaceRuleRegistry {

    static SurfaceRuleRegistry get(Dimension dimension) {
        return SurfaceRuleRegistryImpl.get(dimension);
    }

    void registerRuleSource(String modId, SurfaceRules.RuleSource ruleSource);

    enum Dimension {
        THE_END,
        OVERWORLD,
        NETHER
    }
}
