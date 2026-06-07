package de.realleoxian.moonlightcore.api.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface ModelLayerRegistrar {
    ModelLayerLocation register(ModelLayerLocation location, Supplier<LayerDefinition> layerDefinitionSup);

    default ModelLayerLocation register(ResourceLocation location, String layer, Supplier<LayerDefinition> layerDefinitionSup) {
        return register(new ModelLayerLocation(location, layer), layerDefinitionSup);
    }

    default ModelLayerLocation register(ResourceLocation location, Supplier<LayerDefinition> layerDefinitionSup) {
        return register(location, "main", layerDefinitionSup);
    }
}
