package de.leoxian.moonlightcore.neoforge.client.model;

import de.leoxian.moonlightcore.client.model.ModelLayerRegistrar;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.function.Supplier;

public record NeoforgeModelLayerRegistrar(EntityRenderersEvent.RegisterLayerDefinitions event) implements ModelLayerRegistrar {
    @Override
    public void register(ModelLayerLocation location, Supplier<LayerDefinition> sup) {
        event.registerLayerDefinition(location, sup);
    }
}
