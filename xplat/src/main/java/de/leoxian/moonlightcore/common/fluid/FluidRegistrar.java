package de.leoxian.moonlightcore.common.fluid;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface FluidRegistrar {
	/// Configure and register modded fluids
	/// @param namespace The mod's id
	/// @param initializer The initializer
	static void configure(String namespace, Consumer<FluidRegistrar> initializer) {
		XplatAbstraction.INSTANCE.fluids(namespace, initializer);
	}

	/// Register a new modded fluid with its dynamic attributes and behavior. This method registers automatically the next things:
	/// - The fluid's source under the same id it's given. Example: 'examplemod:slime'
	/// - The fluid's flowing variant with the '_flowing' suffix. Example: 'examplemod:slime_flowing'
	/// - The fluid's bucket item with the '_bucket' suffix. Example: 'examplemod:slime_bucket'
	/// - The fluid's liquid block, which its under the same id as the source fluid. Example: 'examplemod:slime'
	/// @param id The identifier of the fluid
	/// @param fluidType The fluid's type tag
	/// @param properties The immutable fluid properties
	/// @param propertiesHandler The attributes handler of the fluid
	/// @param entityInteraction The entity-interaction fluid behavior
	void register(String id, TagKey<Fluid> fluidType, FluidProperties properties, FluidAttributesHandler propertiesHandler, FluidBehavior entityInteraction);
}
