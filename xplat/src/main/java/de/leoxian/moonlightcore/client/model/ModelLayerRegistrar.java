package de.leoxian.moonlightcore.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Supplier;

public interface ModelLayerRegistrar {
    void register(ModelLayerLocation location, Supplier<LayerDefinition> sup);
}
