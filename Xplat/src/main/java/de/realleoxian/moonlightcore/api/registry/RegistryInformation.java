package de.realleoxian.moonlightcore.api.registry;

import de.realleoxian.moonlightcore.impl.registry.RegistryInformationImpl;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface RegistryInformation<T> {

    static <T> RegistryInformation<T> create() {
        return RegistryInformationImpl.create();
    }

    static <T> RegistryInformation<T> create(ResourceKey<Registry<T>> name) {
        return RegistryInformationImpl.create(name);
    }

    RegistryInformation<T> name(ResourceKey<Registry<T>> name);

    RegistryInformation<T> defaultKey(ResourceLocation defaultKey);

    RegistryInformation<T> synced(boolean synced);

    ResourceKey<Registry<T>> name();

    @Nullable
    ResourceLocation defaultKey();

    boolean isSync();

}
