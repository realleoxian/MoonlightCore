package de.leoxian.moonlightcore.neoforge.common.registry;

import de.leoxian.moonlightcore.common.registry.RegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class NeoforgeRegistryBuilder<R> implements RegistryBuilder<R> {
    private final net.neoforged.neoforge.registries.RegistryBuilder<R> builder;

    public NeoforgeRegistryBuilder(ResourceKey<? extends Registry<R>> registryKey) {
        this.builder = new net.neoforged.neoforge.registries.RegistryBuilder<>(registryKey);
    }

    @Override
    public RegistryBuilder<R> sync(boolean sync) {
        this.builder.sync(sync);
        return this;
    }

    @Override
    public RegistryBuilder<R> defaultId(Identifier id) {
        this.builder.defaultKey(id);
        return this;
    }

    @Override
    public Registry<R> build() {
        return this.builder.create();
    }
}
