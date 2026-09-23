package de.leoxian.moonlightcore.fabric.client.model;

import de.leoxian.moonlightcore.client.model.ModelLayerRegistrar;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Supplier;

public enum FabricModelLayerRegistrar implements ModelLayerRegistrar {
	INSTANCE
	;

	@Override
	public void register(ModelLayerLocation location, Supplier<LayerDefinition> sup) {
		ModelLayerRegistry.registerModelLayer(location, sup::get);
	}
}
