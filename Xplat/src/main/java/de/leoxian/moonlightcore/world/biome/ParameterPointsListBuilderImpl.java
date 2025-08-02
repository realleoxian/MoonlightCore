package de.leoxian.moonlightcore.world.biome;

import com.google.common.collect.ImmutableList;
import de.leoxian.moonlightcore.api.world.biome.ParameterPointsListBuilder;
import net.minecraft.world.level.biome.Climate;

import java.util.ArrayList;
import java.util.List;

public final class ParameterPointsListBuilderImpl implements ParameterPointsListBuilder {
    private static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);

    private final List<Climate.Parameter> temperatures = new ArrayList<>();
    private final List<Climate.Parameter> humidities = new ArrayList<>();
    private final List<Climate.Parameter> continentalnesses = new ArrayList<>();
    private final List<Climate.Parameter> erosions = new ArrayList<>();
    private final List<Climate.Parameter> depths = new ArrayList<>();
    private final List<Climate.Parameter> weirdnesses = new ArrayList<>();
    private final List<Long> offsets = new ArrayList<>();

    @Override
    public ParameterPointsListBuilder temperature(Climate.Parameter parameter) {
        if(!this.temperatures.contains(parameter)) {
            this.temperatures.add(parameter);
        }

        return this;
    }

    @Override
    public ParameterPointsListBuilder humidity(Climate.Parameter humidity) {
        if(!this.humidities.contains(humidity)) {
            this.humidities.add(humidity);
        }

        return this;
    }

    @Override
    public ParameterPointsListBuilder continentalness(Climate.Parameter continentalness) {
        if(!this.continentalnesses.contains(continentalness)) {
            this.continentalnesses.add(continentalness);
        }

        return this;
    }

    @Override
    public ParameterPointsListBuilder erosion(Climate.Parameter erosion) {
        if(!this.erosions.contains(erosion)) {
            this.erosions.add(erosion);
        }

        return this;
    }

    @Override
    public ParameterPointsListBuilder depth(Climate.Parameter depth) {
        if(!this.depths.contains(depth)) {
            this.depths.add(depth);
        }

        return this;
    }

    @Override
    public ParameterPointsListBuilder weirdness(Climate.Parameter weirdness) {
        if(!this.weirdnesses.contains(weirdness)) {
            this.weirdnesses.add(weirdness);
        }

        return this;
    }

    @Override
    public ParameterPointsListBuilder offset(long offset) {
        if(!this.offsets.contains(offset)) {
            this.offsets.add(offset);
        }

        return this;
    }

    @Override
    public List<Climate.ParameterPoint> build() {
        this.populateEmptyParameters();

        ImmutableList.Builder<Climate.ParameterPoint> pointListBuilder = ImmutableList.builder();
        this.temperatures.forEach(temp -> this.humidities.forEach(hum ->
                this.continentalnesses.forEach(con -> this.erosions.forEach(ero ->
                        this.depths.forEach(dep -> this.weirdnesses.forEach(wei ->
                                this.offsets.forEach(off -> pointListBuilder.add(new Climate.ParameterPoint(temp, hum, con, ero, dep, wei, off)))))))));

        return pointListBuilder.build();
    }

    private void populateEmptyParameters() {
        if(this.temperatures.isEmpty()) this.temperatures.add(FULL_RANGE);
        if(this.humidities.isEmpty()) this.humidities.add(FULL_RANGE);
        if(this.continentalnesses.isEmpty()) this.continentalnesses.add(FULL_RANGE);
        if(this.erosions.isEmpty()) this.erosions.add(FULL_RANGE);
        if(this.depths.isEmpty()) this.depths.add(FULL_RANGE);
        if(this.weirdnesses.isEmpty()) this.weirdnesses.add(FULL_RANGE);
        if(this.offsets.isEmpty()) this.offsets.add(0L);
    }
}
