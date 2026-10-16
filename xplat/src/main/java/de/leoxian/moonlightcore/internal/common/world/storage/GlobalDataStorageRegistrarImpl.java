package de.leoxian.moonlightcore.internal.common.world.storage;

import de.leoxian.moonlightcore.common.world.storage.GlobalDataStorage;
import de.leoxian.moonlightcore.common.world.storage.GlobalDataStorageRegistrar;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public record GlobalDataStorageRegistrarImpl(String namespace) implements GlobalDataStorageRegistrar {
	@Override
	public <T extends GlobalDataStorage> void register(Identifier id, Supplier<T> factory) {
		if (GlobalDataStorageManager.loaded) {
			throw new IllegalStateException("May not try to add global data storages when these were already loaded");
		}

		if (GlobalDataStorageManager.FACTORIES.putIfAbsent(id, factory) != null) {
			throw new IllegalStateException("Duplicated global data storage: " + id);
		}
	}

	@Override
	public <T extends GlobalDataStorage> void register(String id, Supplier<T> factory) {
		register(Identifier.fromNamespaceAndPath(namespace, id), factory);
	}
}
