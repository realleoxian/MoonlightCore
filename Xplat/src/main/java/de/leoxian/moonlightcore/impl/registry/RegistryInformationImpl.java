package de.leoxian.moonlightcore.impl.registry;

import de.leoxian.moonlightcore.api.registry.RegistryInformation;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class RegistryInformationImpl<T> implements RegistryInformation<T> {

    public static <T> RegistryInformation<T> create() {
        return new RegistryInformationImpl<>();
    }

    public static <T> RegistryInformation<T> create(ResourceKey<Registry<T>> name) {
        return RegistryInformationImpl.<T>create().name(name);
    }

    private ResourceKey<Registry<T>> name;
    private @Nullable ResourceLocation defaultKey = null;
    private boolean synced = false;

    private RegistryInformationImpl() {}

    @Override
    public RegistryInformation<T> name(ResourceKey<Registry<T>> name) {
        this.name = name;
        return this;
    }

    @Override
    public RegistryInformation<T> defaultKey(ResourceLocation defaultKey) {
        this.defaultKey = defaultKey;
        return this;
    }

    @Override
    public RegistryInformation<T> synced(boolean synced) {
        this.synced = synced;
        return this;
    }

    @Override
    public ResourceKey<Registry<T>> name() {
        return name;
    }

    @Override
    public @Nullable ResourceLocation defaultKey() {
        return defaultKey;
    }

    @Override
    public boolean isSync() {
        return synced;
    }
}
