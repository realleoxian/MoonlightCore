package de.leoxian.moonlightcore.api.world.biome;

import de.leoxian.moonlightcore.world.biome.ParameterPointsListBuilderImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import java.util.function.BiConsumer;

public interface ParameterPointsListBuilder {

    static ParameterPointsListBuilder builder() {
        return new ParameterPointsListBuilderImpl();
    }

    ParameterPointsListBuilder temperature(Climate.Parameter parameter);

    ParameterPointsListBuilder humidity(Climate.Parameter humidity);

    ParameterPointsListBuilder continentalness(Climate.Parameter continentalness);

    ParameterPointsListBuilder erosion(Climate.Parameter erosion);

    ParameterPointsListBuilder depth(Climate.Parameter depth);

    ParameterPointsListBuilder weirdness(Climate.Parameter weirdness);

    ParameterPointsListBuilder offset(long offset);

    List<Climate.ParameterPoint> build();

    default void bind(ResourceKey<Biome> biomeKey, BiConsumer<ResourceKey<Biome>, Climate.ParameterPoint> output) {
        List<Climate.ParameterPoint> points = this.build();

        for(Climate.ParameterPoint point : points) {
            output.accept(biomeKey, point);
        }
    }

}
