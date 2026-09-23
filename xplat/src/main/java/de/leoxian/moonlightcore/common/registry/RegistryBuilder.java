package de.leoxian.moonlightcore.common.registry;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public interface RegistryBuilder<R> {
	/// Create a new builder for a registry that will get registered automatically
	/// @param key The key of the registry
	static <R> RegistryBuilder<R> of(ResourceKey<Registry<R>> key) {
		return XplatAbstraction.INSTANCE.registryBuilder(key);
	}

	/// Sets whether this registry should sync or not
	/// @param sync If the registry will sync or not
	RegistryBuilder<R> sync(boolean sync);

	/// Sets the default identifier entry of this registry
	/// @param id The default id/key
	RegistryBuilder<R> defaultId(Identifier id);

	/// @return A new static registry that will be automatically registered
	Supplier<Registry<R>> build();
}
