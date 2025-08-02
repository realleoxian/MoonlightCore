package de.leoxian.moonlightcore.world.surface;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.api.world.surface.SurfaceRuleRegistry;
import de.leoxian.moonlightcore.mixin.SurfaceRulesContextAccessor;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.HashMap;
import java.util.Map;

public final class SurfaceRuleRegistryImpl implements SurfaceRuleRegistry {
    private static final Map<Dimension,SurfaceRuleRegistryImpl> REGISTRIES = new HashMap<>();

    public static SurfaceRuleRegistryImpl get(Dimension dimension) {
        return REGISTRIES.computeIfAbsent(dimension, k -> new SurfaceRuleRegistryImpl());
    }

    private final Map<String, SurfaceRules.RuleSource> moddedRuleSources = new HashMap<>();

    SurfaceRuleRegistryImpl() {}

    @Override
    public void registerRuleSource(String modId, SurfaceRules.RuleSource ruleSource) {
        if(!this.moddedRuleSources.containsKey(modId)) {
            this.moddedRuleSources.put(modId, ruleSource);
        }
    }

    public NamespacedRuleSource merge (SurfaceRules.RuleSource fallback) {
        return new NamespacedRuleSource(this.moddedRuleSources, fallback);
    }

    public record NamespacedRuleSource (Map<String, SurfaceRules.RuleSource> moddedRules, SurfaceRules.RuleSource fallback) implements SurfaceRules.RuleSource {
        private static final KeyDispatchDataCodec<NamespacedRuleSource> CODEC = KeyDispatchDataCodec.of(RecordCodecBuilder.create(instance -> instance.group(
                Codec.unboundedMap(Codec.STRING, SurfaceRules.RuleSource.CODEC).fieldOf("moddedRules").forGetter(NamespacedRuleSource::moddedRules),
                SurfaceRules.RuleSource.CODEC.fieldOf("fallback").forGetter(NamespacedRuleSource::fallback)).apply(instance, NamespacedRuleSource::new)));

        @Override
        public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
            ImmutableMap.Builder<String, SurfaceRules.SurfaceRule> builtModdedRules = ImmutableMap.builder();
            this.moddedRules.entrySet().forEach(entry -> builtModdedRules.put(entry.getKey(), entry.getValue().apply(context)));

            return new NamespaceSurfaceRule(context, builtModdedRules.build(), fallback.apply(context));
        }

        @Override
        public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
            return CODEC;
        }

        record NamespaceSurfaceRule (SurfaceRules.Context context, Map<String, SurfaceRules.SurfaceRule> moddedRules, SurfaceRules.SurfaceRule fallback) implements SurfaceRules.SurfaceRule {
            @Override
            public BlockState tryApply(int x, int y, int z) {
                Holder<Biome> biomeHolder = ((SurfaceRulesContextAccessor) (Object) context).mlcore_getBiome().get();
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
