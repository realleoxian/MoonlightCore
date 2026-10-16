package de.leoxian.moonlightcore.common.world.storage;

import de.leoxian.moonlightcore.internal.common.world.storage.GlobalDataStorageRegistrarImpl;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface GlobalDataStorageRegistrar {
	/// Configure and register custom global data storage factories that are used to have custom data across the server
	/// @param namespace The namespace of the mod id
	/// @param initializer The initializer of the registrar
	static void configure(final String namespace, Consumer<GlobalDataStorageRegistrar> initializer) {
		initializer.accept(new GlobalDataStorageRegistrarImpl(namespace));
	}

	/// Register a global data storage type factory to have global data across the server
	/// @param id The id of the storage
	/// @param factory The factory
	/// @param <T> The implementation's type
	<T extends GlobalDataStorage> void register(Identifier id, Supplier<T> factory);

	/// Register a global data storage type factory to have global data across the server
	/// @param id The id of the storage
	/// @param factory The factory
	/// @param <T> The implementation's type
	<T extends GlobalDataStorage> void register(final String id, Supplier<T> factory);
}
