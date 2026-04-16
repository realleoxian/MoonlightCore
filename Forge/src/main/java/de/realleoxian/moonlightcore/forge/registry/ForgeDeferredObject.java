package de.realleoxian.moonlightcore.forge.registry;

import de.realleoxian.moonlightcore.api.registry.DeferredObject;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Predicate;

public final class ForgeDeferredObject<R, T extends R> implements DeferredObject<R, T> {
    private final ResourceKey<R> key;
    private final RegistryObject<T> wrapped;

    public ForgeDeferredObject(ResourceKey<R> key, RegistryObject<T> wrapped) {
        this.key = key;
        this.wrapped = wrapped;
    }

    @Override
    public T get() {
        return this.wrapped.get();
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> filter) {
        Holder<R> holder = asHolder();
        return holder != null && holder.is(filter);
    }

    @Override
    public boolean is(ResourceKey<R> key) {
        Holder<R> holder = asHolder();
        return holder != null && holder.is(key);
    }

    @Override
    public boolean is(TagKey<R> tag) {
        Holder<R> holder = asHolder();
        return holder != null && holder.is(tag);
    }

    @Override
    public boolean isBound() {
        return this.wrapped.isPresent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable Holder<R> asHolder() {
        return (Holder<R>) this.wrapped.getHolder().orElse(null);
    }

    @Override
    public ResourceLocation name() {
        return key.location();
    }

    @Override
    public ResourceKey<? extends Registry<R>> registryKey() {
        return ResourceKey.createRegistryKey(key.registry());
    }
}
