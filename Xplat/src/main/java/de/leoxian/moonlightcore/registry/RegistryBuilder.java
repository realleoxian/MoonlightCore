package de.leoxian.moonlightcore.registry;

import com.mojang.serialization.Lifecycle;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public final class RegistryBuilder<T> {

    public static <T> Registry<T> build(ResourceKey<? extends Registry<T>> registryKey) {
        return build(registryKey, $ -> {});
    }

    public static <T> Registry<T> build(ResourceKey<? extends Registry<T>> registryKey, Consumer<RegistryBuilder<T>> builderCallback) {
        RegistryBuilder<T> builder = create(registryKey);
        builderCallback.accept(builder);

        return builder.build();
    }

    public static <T> RegistryBuilder<T> create(ResourceKey<? extends Registry<T>> registryKey) {
        return new RegistryBuilder<>(registryKey);
    }

    private final ResourceKey<? extends Registry<T>> registryKey;
    @Nullable
    private ResourceLocation defaultKey;

    private RegistryBuilder(ResourceKey<? extends Registry<T>> registryKey) {
        this.registryKey = registryKey;
    }

    public RegistryBuilder<T> defaultKey(ResourceLocation key) {
        this.defaultKey = key;
        return this;
    }

    public RegistryBuilder<T> defaultKey(ResourceKey<T> key) {
        return this.defaultKey(key.location());
    }

    public Registry<T> build() {
        return this.defaultKey != null
                ? new DefaultedMappedRegistry<>(this.defaultKey.toString(), this.registryKey, Lifecycle.stable(), false)
                : new MappedRegistry<>(this.registryKey, Lifecycle.stable(), false);
    }
}
