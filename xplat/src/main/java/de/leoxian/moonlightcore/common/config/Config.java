package de.leoxian.moonlightcore.common.config;

import de.leoxian.moonlightcore.common.config.file.LoadedConfig;
import de.leoxian.moonlightcore.internal.common.config.ConfigRegistry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.function.Function;

@ApiStatus.NonExtendable
public interface Config<T> {
	/// Registers a new local config
	/// @param id The identifier of this config
	/// @param factory The factory method of this config's instance
	static <O> Config<O> registerLocal(Identifier id, Function<ConfigSchema.Builder, O> factory) {
		return ConfigRegistry.register(id, factory, false);
	}

	/// Registers a new synced config that will send all the data to the joining players on a server
	/// @param id The identifier of this config
	/// @param factory The factory method of this config's instance
	static <O> Config<O> registerSynced(Identifier id, Function<ConfigSchema.Builder, O> factory) {
		return ConfigRegistry.register(id, factory, true);
	}

	/// @return The instance of the config's type
	T instance();

	/// @return This config's id
	Identifier id();

	/// @return The root schema of this config
	ConfigSchema schema();

	/// @return Where the file its at
	Path filePath();

	/// @return The current loaded data on this config
	LoadedConfig loadedConfig();
}
