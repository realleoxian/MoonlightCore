package de.leoxian.moonlightcore.client.model;

import de.leoxian.moonlightcore.client.platform.XplatClientAbstraction;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ModelLayerRegistrar {
	/// Configure and register model layer definitions
	/// @param namespace The mod's id to add this registrar to
	/// @param initializer The initializer of the registrar
	static void configure(String namespace, Consumer<ModelLayerRegistrar> initializer) {
		XplatClientAbstraction.INSTANCE.modelLayers(namespace, initializer);
	}

	/// Register a new model layer definition
	/// @param location The location of the layer definition
	/// @param sup The layer definition
	void register(ModelLayerLocation location, Supplier<LayerDefinition> sup);
}
