package de.leoxian.moonlightcore.fabric.common.level.biome.modifier;

import de.leoxian.moonlightcore.common.level.biome.modifier.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.modifier.AttributeModifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;
import java.util.function.BiPredicate;

public enum FabricBiomeModification implements BiomeModification {
    INSTANCE
    ;

    @Override
    public void modify(Identifier id, BiomeModificationPhase phase, BiomeSelector selector, BiomeModifier modifier) {
        ModificationPhase fabricPhase = switch (phase) {
            case ADDITION -> ModificationPhase.ADDITIONS;
            case REMOVAL -> ModificationPhase.REMOVALS;
            case POST_PROCESSING -> ModificationPhase.POST_PROCESSING;
        };

        BiomeModifications.create(id).add(fabricPhase, ctx -> selector.test(ctx.getBiomeHolder()), (selectionContext, modificationContext) -> {
            Context context = new Context(modificationContext);
            modifier.modifyBiome(selectionContext.getBiomeHolder(), context);
        });
    }

    private record Context(BiomeModificationContext.ClimateContext climate, BiomeModificationContext.GenerationContext generation, BiomeModificationContext.MobSpawnContext mobSpawn,
                           BiomeModificationContext.EffectsContext effects) implements BiomeModificationContext {

        private Context(net.fabricmc.fabric.api.biome.v1.BiomeModificationContext fabricCtx) {
            this(new ClimateContextImpl(fabricCtx.getWeather()), new GenerationContextImpl(fabricCtx.getGenerationSettings()),
                    new MobSpawnContextImpl(fabricCtx.getMobSpawnSettings()), new EffectsContextImpl(fabricCtx.getEffects()));
        }

        private record ClimateContextImpl(net.fabricmc.fabric.api.biome.v1.BiomeModificationContext.WeatherContext fabricCtx) implements ClimateContext {
            @Override
            public void hasPrecipitation(boolean hasPrecipitation) {
                fabricCtx.setPrecipitation(hasPrecipitation);
            }

            @Override
            public void temperature(float temperature) {
                fabricCtx.setTemperature(temperature);
            }

            @Override
            public void temperatureModifier(Biome.TemperatureModifier temperatureModifier) {
                fabricCtx.setTemperatureModifier(temperatureModifier);
            }

            @Override
            public void downfall(float downfall) {
                fabricCtx.setDownfall(downfall);
            }
        }

        private record GenerationContextImpl(net.fabricmc.fabric.api.biome.v1.BiomeModificationContext.GenerationSettingsContext fabricCtx) implements GenerationContext {
            @Override
            public void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> key) {
                fabricCtx.addFeature(step, key);
            }

            @Override
            public boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> key) {
                return fabricCtx.removeFeature(step, key);
            }

            @Override
            public void addCarver(ResourceKey<ConfiguredWorldCarver<?>> key) {
                fabricCtx.addCarver(key);
            }

            @Override
            public boolean removeCarver(ResourceKey<ConfiguredWorldCarver<?>> key) {
                return fabricCtx.removeCarver(key);
            }
        }

        private record MobSpawnContextImpl(net.fabricmc.fabric.api.biome.v1.BiomeModificationContext.MobSpawnSettingsContext fabricCtx) implements MobSpawnContext {
            @Override
            public void creatureGenerationProbability(float creatureGenerationProbability) {
                fabricCtx.setCreatureGenerationProbability(creatureGenerationProbability);
            }

            @Override
            public void addMobCharge(EntityType<?> entityType, double charge, double energyBudget) {
                fabricCtx.addMobCharge(entityType, charge, energyBudget);
            }

            @Override
            public void addSpawn(MobCategory category, int weight, MobSpawnSettings.SpawnerData spawnerData) {
                fabricCtx.addSpawn(category, spawnerData, weight);
            }

            @Override
            public boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> filter) {
                return fabricCtx.removeSpawns(filter);
            }
        }

        private record EffectsContextImpl(net.fabricmc.fabric.api.biome.v1.BiomeModificationContext.EffectsContext fabricCtx) implements EffectsContext {
            @Override
            public void waterColor(int waterColor) {
                fabricCtx.setWaterColor(waterColor);
            }

            @Override
            public void foliageColorOverride(int foliageColorOverride) {
                fabricCtx.setFoliageColorOverride(foliageColorOverride);
            }

            @Override
            public void dryFoliageColorOverride(int dryFoliageColorOverride) {
                fabricCtx.setDryFoliageColorOverride(dryFoliageColorOverride);
            }

            @Override
            public void grassColorOverride(int grassColorOverride) {
                fabricCtx.setGrassColorOverride(grassColorOverride);
            }

            @Override
            public void grassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
                fabricCtx.setGrassColorModifier(grassColorModifier);
            }
        }
    }
}
