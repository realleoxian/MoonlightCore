package de.leoxian.moonlightcore.internal.common.level;

import com.google.common.collect.ImmutableList;
import de.leoxian.moonlightcore.common.level.SurfaceRuleRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SurfaceRuleRegistryImpl implements SurfaceRuleRegistry {
    private static final Map<ResourceKey<LevelStem>, SurfaceRuleRegistry> INSTANCE = Collections.synchronizedMap(new HashMap<>());

    public static SurfaceRuleRegistry get(ResourceKey<LevelStem> key) {
        return INSTANCE.computeIfAbsent(key, _ -> new SurfaceRuleRegistryImpl());
    }

    private final ImmutableList.Builder<SurfaceRules.RuleSource> rules = new ImmutableList.Builder<>();
    private SurfaceRules.RuleSource cached = null;
    private boolean dirty = true;

    @Override
    public void add(SurfaceRules.RuleSource ruleSource) {
        this.rules.add(ruleSource);
        this.dirty = true;
    }

    @ApiStatus.Internal
    public SurfaceRules.RuleSource getOrCreate(SurfaceRules.RuleSource source) {
        if (this.dirty || this.cached == null) {
            ImmutableList.Builder<SurfaceRules.RuleSource> list = ImmutableList.builder();
            list.add(source);
            list.addAll(this.rules.build());
            this.cached = SurfaceRules.sequence(list.build().toArray(SurfaceRules.RuleSource[]::new));
            this.dirty = false;
        }
        return this.cached;
    }
}
