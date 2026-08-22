package de.leoxian.moonlightcore.client.model;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ModelLayerRegistrar {
    static void init(String namespace, Consumer<ModelLayerRegistrar> initializer) {
        XplatClientAbstraction.INSTANCE.modelLayers(namespace, initializer);
    }

    void register(ModelLayerLocation location, Supplier<LayerDefinition> sup);
}
