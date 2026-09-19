package de.leoxian.moonlightcore.fabric.common.pack;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.pack.DataPackRegistryRegistrar;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

public enum FabricDataPackRegistryRegistrar implements DataPackRegistryRegistrar {
    INSTANCE
    ;

    @Override
    public <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        if (networkCodec == null) {
            DynamicRegistries.register(registryKey, codec);
        } else {
            DynamicRegistries.registerSynced(registryKey, codec, networkCodec, DynamicRegistries.SyncOption.SKIP_WHEN_EMPTY);
        }
    }
}
