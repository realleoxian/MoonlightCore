package de.leoxian.moonlightcore.common.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DeferredHolder<R, T extends R> implements Holder<R>, Supplier<T> {
    public static  <R, T extends R> DeferredHolder<R, T> create(ResourceKey<R> key) {
        return new DeferredHolder<>(key);
    }

    public static <R, T extends R> DeferredHolder<R, T> create(ResourceKey<? extends Registry<R>> registryKey, Identifier id) {
        return create(ResourceKey.create(registryKey, id));
    }

    public static <R, T extends R> DeferredHolder<R, T> create(Registry<R> registry, Identifier id) {
        return create(registry.key(), id);
    }

    private final ResourceKey<R> key;
    private Holder<T> holder = null;

    private DeferredHolder(ResourceKey<R> key) {
        this.key = key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) value();
    }

    @Override
    public R value() {
        tryBind(true);
        return Objects.requireNonNull(this.holder, "Unable to get registry holder '" + this.key + "'").value();
    }

    @Override
    public boolean isBound() {
        tryBind(false);
        return this.holder != null && this.holder.isBound();
    }

    @Override
    public boolean areComponentsBound() {
        tryBind(false);
        return this.holder != null && this.holder.areComponentsBound();
    }

    @Override
    public boolean is(Identifier key) {
        return this.key.identifier().equals(key);
    }

    @Override
    public boolean is(ResourceKey<R> key) {
        return this.key == key;
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return predicate.test(this.key);
    }

    @Override
    public boolean is(TagKey<R> tag) {
        tryBind(false);
        return this.holder != null && ((Holder<R>) this.holder).is(tag);
    }

    @Override
    public boolean is(Holder<R> holder) {
        tryBind(false);
        return this.holder != null && ((Holder<R>) this.holder).is(holder);
    }

    @Override
    public Stream<TagKey<R>> tags() {
        tryBind(false);
        return this.holder != null ? ((Holder<R>) this.holder).tags() : Stream.empty();
    }

    @Override
    public DataComponentMap components() {
        tryBind(false);
        return this.holder == null ? DataComponentMap.EMPTY : this.holder.components();
    }

    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return Either.left(this.key);
    }

    @Override
    public Optional<ResourceKey<R>> unwrapKey() {
        return Optional.of(this.key);
    }

    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<R> registry) {
        tryBind(false);
        return this.holder != null && ((Holder<R>) this.holder).canSerializeIn(registry);
    }

    public ResourceKey<R> getKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    public final void tryBind(boolean throwOnMissingRegistry) {
        if (this.holder != null) return;

        final var registry = (Registry<R>) BuiltInRegistries.REGISTRY.get(this.key.registry()).orElse(null);
        if (registry != null) {
            this.holder = (Holder<T>) registry.get(this.key).orElse(null);
        } else if (throwOnMissingRegistry) {
            throw new IllegalArgumentException("Unknown registry '" + this.key.registry() + "'");
        }
    }
}
