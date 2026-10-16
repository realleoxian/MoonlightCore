package de.leoxian.moonlightcore.common.world.storage;

import de.leoxian.moonlightcore.common.util.ValueIOSerializable;
import de.leoxian.moonlightcore.internal.common.world.storage.GlobalDataStorageManager;
import net.minecraft.resources.Identifier;

public interface GlobalDataStorage extends ValueIOSerializable {
	/// Get or create a new instance of `T`
	/// @param id The id of the data
	/// @return An instance of the global data storage
	/// @param <T> The storage's type
	/// @throws IllegalStateException if there is no instance of the storage and there is no factory registered
	static <T extends GlobalDataStorage> T getOrCreate(final Identifier id) {
		return GlobalDataStorageManager.getOrCreate(id);
	}

	/// Reset all data on this storage
	void reset();
}
