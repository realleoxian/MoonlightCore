package de.leoxian.moonlightcore.api.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface ModelLayerRegistrar {
    ModelLayerLocation register(ModelLayerLocation location, Supplier<LayerDefinition> layerDefinition);

    default ModelLayerLocation register(ResourceLocation location, String layer, Supplier<LayerDefinition> layerDefinition) {
        return register(new ModelLayerLocation(location, layer), layerDefinition);
    }

    default ModelLayerLocation register(ResourceLocation location, Supplier<LayerDefinition> layerDefinition) {
        return register(location, "main", layerDefinition);
    }
}
