package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public abstract class AbstractBuilder<R, T extends R> implements Builder<R, T> {
    protected final ResourceKey<? extends Registry<R>> registryType;
    protected final ResourceLocation id;
    protected final ResourceKey<R> key;

    protected AbstractBuilder(ResourceKey<? extends Registry<R>> registryType, ResourceLocation id) {
        this.registryType = registryType;
        this.id = id;

        this.key = ResourceKey.create(registryType, id);
    }

    protected abstract T buildEntry();

    @Override
    public RegistryEntry<R, T> build(Consumer<RegistryEntry<R, ?>> output) {
        RegistryEntry<R, T> entry = RegistryEntry.create(key, this::buildEntry);
        output.accept(entry);

        return entry;
    }

    @Override
    public ResourceKey<? extends Registry<R>> registryKey() {
        return this.registryType;
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }
}
