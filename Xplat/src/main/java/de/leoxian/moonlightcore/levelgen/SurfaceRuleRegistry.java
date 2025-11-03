package de.leoxian.moonlightcore.levelgen;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.mixin.accessor.SurfaceRulesContextAccessor;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public final class SurfaceRuleRegistry {
    private static final Map<Dimension, SurfaceRuleRegistry> REGISTRIES = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    static {
        get(Dimension.THE_END).registerRuleSource("minecraft", SurfaceRuleData.end());
        get(Dimension.OVERWORLD).registerRuleSource("minecraft", SurfaceRuleData.overworld());
        get(Dimension.NETHER).registerRuleSource("minecraft", SurfaceRuleData.nether());
    }

    public static SurfaceRuleRegistry get(Dimension dimension) {
        return REGISTRIES.computeIfAbsent(dimension, $ -> new SurfaceRuleRegistry());
    }

    @ApiStatus.Internal
    public static SurfaceRules.RuleSource getRuleSource(Dimension levelStemKey, SurfaceRules.RuleSource fallback) {
        return new NamespacedRuleSource(get(levelStemKey).moddedRuleSources, fallback);
    }

    private final Map<String, SurfaceRules.RuleSource> moddedRuleSources = new HashMap<>();

    private SurfaceRuleRegistry() {}

    public void registerRuleSource(String modId, SurfaceRules.RuleSource ruleSource) {
        if(this.moddedRuleSources.putIfAbsent(modId, ruleSource) != null) {
            LOGGER.warn("Encountered duplicated RuleSource for '{}'", modId);
        }
    }

    public enum Dimension {
        THE_END,
        OVERWORLD,
        NETHER
    }

    public record NamespacedRuleSource (Map<String, SurfaceRules.RuleSource> moddedRules, SurfaceRules.RuleSource fallback) implements SurfaceRules.RuleSource {
        public static final KeyDispatchDataCodec<NamespacedRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, SurfaceRules.RuleSource.CODEC).fieldOf("rules").forGetter(NamespacedRuleSource::moddedRules),
                SurfaceRules.RuleSource.CODEC.fieldOf("fallback").forGetter(NamespacedRuleSource::fallback)).apply(instance, NamespacedRuleSource::new)));

        @Override
        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
            ImmutableMap.Builder<String, SurfaceRules.SurfaceRule> builtModdedRules = ImmutableMap.builder();
            this.moddedRules.forEach((key, value) -> builtModdedRules.put(key, value.apply(context)));

            return new NamespaceSurfaceRule(context, builtModdedRules.build(), fallback.apply(context));
        }

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return CODEC;
        }

        private record NamespaceSurfaceRule (SurfaceRules.Context context, Map<String, SurfaceRules.SurfaceRule> moddedRules, SurfaceRules.SurfaceRule fallback) implements SurfaceRules.SurfaceRule {
            @Override
            public BlockState tryApply(int x, int y, int z) {
                Holder<Biome> biomeHolder = ((SurfaceRulesContextAccessor) (Object) context).getBiome().get();
                BlockState state = null;

                if (biomeHolder.is(key -> moddedRules.containsKey(key.location().getNamespace()))) {
                    state = this.moddedRules.get(biomeHolder.unwrapKey().get().location().getNamespace()).tryApply(x, y, z);
                }

                if (state == null) {
                    state = fallback.tryApply(x, y, z);
                }

                return state;
            }
        }
    }
}
