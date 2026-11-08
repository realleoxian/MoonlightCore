package de.leoxian.moonlightcore.common.level.biome.modifier;

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
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.BiPredicate;

@ApiStatus.NonExtendable
public interface BiomeModificationContext {
    ClimateContext climate();

    GenerationContext generation();

    MobSpawnContext mobSpawn();

    EffectsContext effects();

    interface ClimateContext {
        void hasPrecipitation(boolean hasPrecipitation);

        void temperature(float temperature);

        void temperatureModifier(Biome.TemperatureModifier temperatureModifier);

        void downfall(float downfall);
    }

    interface GenerationContext {
        void addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> key);

        boolean removeFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> key);

        default boolean removeFeature(ResourceKey<PlacedFeature> key) {
            boolean anyRemoved = false;
            for (final GenerationStep.Decoration step : GenerationStep.Decoration.values()) {
                if (removeFeature(step, key)) {
                    anyRemoved = true;
                }
            }
            return anyRemoved;
        }

        void addCarver(ResourceKey<ConfiguredWorldCarver<?>> key);

        boolean removeCarver(ResourceKey<ConfiguredWorldCarver<?>> key);
    }

    interface MobSpawnContext {
        void creatureGenerationProbability(float creatureGenerationProbability);

        void addMobCharge(EntityType<?> entityType, double charge, double energyBudget);

        void addSpawn(MobCategory category, int weight, MobSpawnSettings.SpawnerData spawnerData);

        default void addSpawn(MobCategory category, int weight, EntityType<?> entityType, int min, int max) {
            addSpawn(category, weight, new MobSpawnSettings.SpawnerData(entityType, min, max));
        }

        boolean removeSpawns(BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> filter);
    }

    interface EffectsContext {
        void waterColor(int waterColor);

        void foliageColorOverride(int foliageColorOverride);

        void dryFoliageColorOverride(int dryFoliageColorOverride);

        void grassColorOverride(int grassColorOverride);

        void grassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier);
    }
}
