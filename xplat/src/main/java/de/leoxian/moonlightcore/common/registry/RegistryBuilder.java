package de.leoxian.moonlightcore.common.registry;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface RegistryBuilder<R> {
    static <R> RegistryBuilder<R> of(ResourceKey<Registry<R>> key) {
        return XplatAbstraction.INSTANCE.registryBuilder(key);
    }

    RegistryBuilder<R> sync(boolean sync);

    RegistryBuilder<R> defaultId(Identifier id);

    Registry<R> build();
}
