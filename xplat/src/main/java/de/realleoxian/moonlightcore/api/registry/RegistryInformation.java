package de.realleoxian.moonlightcore.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class RegistryInformation {
    public static RegistryInformation create(ResourceKey<? extends Registry<?>> registryKey) {
        return new RegistryInformation(registryKey);
    }

    public static RegistryInformation create(ResourceLocation registryKey) {
        return create(ResourceKey.createRegistryKey(registryKey));
    }

    private final ResourceKey<? extends Registry<?>> registryKey;
    @Nullable
    private ResourceLocation defaultKey = null;
    private boolean synced = false;

    private RegistryInformation(ResourceKey<? extends Registry<?>> registryKey) {
        this.registryKey = Objects.requireNonNull(registryKey, "Registry key may not be 'null'");
    }

    public RegistryInformation synced(boolean synced) {
        this.synced = synced;
        return this;
    }

    public RegistryInformation defaultKey(ResourceLocation defaultKey) {
        this.defaultKey = defaultKey;
        return this;
    }

    public ResourceKey<? extends Registry<?>> registryKey() {
        return registryKey;
    }

    public @Nullable ResourceLocation defaultKey() {
        return defaultKey;
    }

    public boolean synced() {
        return synced;
    }
}
