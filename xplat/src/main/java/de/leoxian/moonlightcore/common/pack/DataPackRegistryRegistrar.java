package de.leoxian.moonlightcore.common.pack;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

public interface DataPackRegistryRegistrar {
    <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec);

    default <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec) {
        register(registryKey, codec, null);
    }
}
