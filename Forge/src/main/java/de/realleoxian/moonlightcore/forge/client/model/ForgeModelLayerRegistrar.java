package de.realleoxian.moonlightcore.forge.client.model;

import de.realleoxian.moonlightcore.api.client.model.ModelLayerRegistrar;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ForgeModelLayerRegistrar implements ModelLayerRegistrar {
    private final Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions = new HashMap<>();

    @SubscribeEvent
    public void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        this.layerDefinitions.forEach(event::registerLayerDefinition);
    }

    @Override
    public ModelLayerLocation register(ModelLayerLocation location, Supplier<LayerDefinition> layerDefinition) {
        this.layerDefinitions.put(location, layerDefinition);
        return location;
    }
}
