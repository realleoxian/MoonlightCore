package de.leoxian.moonlightcore.neoforge.common.pack;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.pack.DataPackRegistryRegistrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NeoforgeDataPackRegistryRegistrar implements DataPackRegistryRegistrar {
    private record Entry<T>(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        void register(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(registryKey, codec, networkCodec);
        }
    }

    private final List<Entry<?>> entries = new ArrayList<>();

    @SubscribeEvent
    public void onDatapackNewRegistry(DataPackRegistryEvent.NewRegistry event) {
        this.entries.forEach(e -> e.register(event));
    }

    @Override
    public <T> void register(ResourceKey<Registry<T>> registryKey, Codec<T> codec, @Nullable Codec<T> networkCodec) {
        this.entries.add(new Entry<>(registryKey, codec, networkCodec));
    }
}
