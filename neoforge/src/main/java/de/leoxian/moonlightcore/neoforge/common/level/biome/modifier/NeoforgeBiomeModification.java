package de.leoxian.moonlightcore.neoforge.common.level.biome.modifier;

import com.mojang.datafixers.util.Pair;
import de.leoxian.moonlightcore.common.level.biome.modifier.*;
import de.leoxian.moonlightcore.common.level.biome.modifier.BiomeModifier;
import de.leoxian.moonlightcore.common.platform.Platform;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiPredicate;

public enum NeoforgeBiomeModification implements BiomeModification {
    INSTANCE
    ;
    private static final Logger LOGGER = LoggerFactory.getLogger(NeoforgeBiomeModification.class);

    static final EnumMap<BiomeModificationPhase, List<Pair<BiomeSelector, BiomeModifier>>> MODIFICATIONS = new EnumMap<>(BiomeModificationPhase.class);

    @Override
    public void modify(Identifier id, BiomeModificationPhase phase, BiomeSelector selector, BiomeModifier modifier) {
        MODIFICATIONS.computeIfAbsent(phase, _ -> new ArrayList<>()).add(Pair.of(selector, modifier));
    }

    public record Context(BiomeModificationContext.ClimateContext climate, BiomeModificationContext.GenerationContext generation,
                           BiomeModificationContext.MobSpawnContext mobSpawn, BiomeModificationContext.EffectsContext effects) implements BiomeModificationContext {

        public Context(ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            this(new ClimateContextImpl(builder.getClimateSettings()), new GenerationContextImpl(builder.getGenerationSettings()),
                    new MobSpawnContextImpl(builder.getMobSpawnSettings()), new EffectsContextImpl(builder.getSpecialEffects()));
        }

        private record ClimateContextImpl(ClimateSettingsBuilder neoBuilder) implements ClimateContext {
            @Override
            public void hasPrecipitation(boolean hasPrecipitation) {
                neoBuilder.setHasPrecipitation(hasPrecipitation);
            }

            @Override
            public void temperature(float temperature) {
                neoBuilder.setTemperature(temperature);
            }

            @Override
            public void temperatureModifier(Biome.TemperatureModifier temperatureModifier) {
                neoBuilder.setTemperatureModifier(temperatureModifier);
            }

            @Override
            public void downfall(float downfall) {
                neoBuilder.setDownfall(downfall);
            }
        }

        private record GenerationContextImpl(BiomeGenerationSettingsBuilder neoBuilder) implements GenerationContext {
            @Override
            public void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> key) {
                final MinecraftServer server = Platform.getCurrentServer();
                if (server != null) {
                    neoBuilder.addFeature(step, server.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).getOrThrow(key));
                } else {
                    LOGGER.warn("Failed to add feature '{}' to biome. No server available.", key);
                }
            }

            @Override
            public boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> key) {
                return neoBuilder.getFeatures(step).removeIf(h -> h.is(key));
            }

            @Override
            public void addCarver(ResourceKey<ConfiguredWorldCarver<?>> key) {
                final MinecraftServer server = Platform.getCurrentServer();
                if (server != null) {
                    neoBuilder.addCarver(server.registries().compositeAccess().lookupOrThrow(Registries.CONFIGURED_CARVER).getOrThrow(key));
                } else {
                    LOGGER.warn("Failed to add carver '{}' to biome. No server available", key);
                }
            }

            @Override
            public boolean removeCarver(ResourceKey<ConfiguredWorldCarver<?>> key) {
                return neoBuilder.getCarvers().removeIf(h -> h.is(key));
            }
        }

        private record MobSpawnContextImpl(MobSpawnSettingsBuilder neoBuilder) implements MobSpawnContext {
            @Override
            public void creatureGenerationProbability(float creatureGenerationProbability) {
                neoBuilder.creatureGenerationProbability(creatureGenerationProbability);
            }

            @Override
            public void addMobCharge(EntityType<?> entityType, double charge, double energyBudget) {
                neoBuilder.addMobCharge(entityType, charge, energyBudget);
            }

            @Override
            public void addSpawn(MobCategory category, int weight, MobSpawnSettings.SpawnerData spawnerData) {
                neoBuilder.addSpawn(category, weight, spawnerData);
            }

            @Override
            public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> filter) {
                boolean anyRemoved = false;
                for (MobCategory group : MobCategory.values()) {
                    if (neoBuilder.getSpawner(group).getList().removeIf(entry -> filter.test(group, entry.value()))) {
                        anyRemoved = true;
                    }
                }

                return anyRemoved;
            }
        }

        private record EffectsContextImpl(BiomeSpecialEffectsBuilder neoBuilder) implements EffectsContext {
            @Override
            public void waterColor(int waterColor) {
                neoBuilder.waterColor(waterColor);
            }

            @Override
            public void foliageColorOverride(int foliageColorOverride) {
                neoBuilder.foliageColorOverride(foliageColorOverride);
            }

            @Override
            public void dryFoliageColorOverride(int dryFoliageColorOverride) {
                neoBuilder.dryFoliageColorOverride(dryFoliageColorOverride);
            }

            @Override
            public void grassColorOverride(int grassColorOverride) {
                neoBuilder.grassColorOverride(grassColorOverride);
            }

            @Override
            public void grassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
                neoBuilder.grassColorModifier(grassColorModifier);
            }
        }
    }
}
