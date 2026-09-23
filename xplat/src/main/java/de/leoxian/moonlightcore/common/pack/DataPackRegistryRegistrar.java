package de.leoxian.moonlightcore.common.pack;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public interface DataPackRegistryRegistrar {
	/// Configure and register modded dynamic registries that may or may not be synced
	/// @param namespace The mod's id the registrar is being added to
	/// @param initializer The registrar initializer
	static void configure(String namespace, Consumer<DataPackRegistryRegistrar> initializer) {
		XplatAbstraction.INSTANCE.datapackRegistries(namespace, initializer);
	}

	/// Registers a new dynamic registry
	/// @param registryKey The registry key
	/// @param codec The codec used on the register
	/// @param networkCodec A codec that can be used at syncing, may be `null` if sync isn't needed
	<T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec);

	/// Registers a new dynamic registry
	/// @param registryKey The registry key
	/// @param codec The codec used on the register
	default <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
		register(registryKey, codec, null);
	}
}
