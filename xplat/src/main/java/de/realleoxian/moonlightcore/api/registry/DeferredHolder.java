package de.realleoxian.moonlightcore.api.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DeferredHolder<R, T extends R> implements Holder<R>, Supplier<T> {
    private final ResourceKey<R> key;
    private Holder<T> holder = null;

    public DeferredHolder(ResourceKey<R> key) {
        this.key = key;
    }

    @Override
    public T get() {
        return value();
    }

    @Override
    public T value() {
        tryBind(true);
        return Objects.requireNonNull(this.holder, "Unable to get registry holder '" + this.key + "'").value();
    }

    @Override
    public boolean isBound() {
        tryBind(false);
        return this.holder != null && this.holder.isBound();
    }

    @Override
    public boolean is(ResourceLocation resourceLocation) {
        return this.key.location().equals(resourceLocation);
    }

    @Override
    public boolean is(ResourceKey<R> resourceKey) {
        return this.key.equals(resourceKey);
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return predicate.test(this.key);
    }

    @Override
    public boolean is(TagKey<R> tagKey) {
        tryBind(false);
        return this.holder != null && ((Holder<R>) this.holder).is(tagKey);
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
    public Either<ResourceKey<R>, R> unwrap() {
        tryBind(false);
        return this.holder == null ? Either.left(this.key) : Either.right(this.holder.value());
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
    public boolean canSerializeIn(HolderOwner<R> holderOwner) {
        tryBind(false);
        return this.holder != null && ((Holder<R>) this.holder).canSerializeIn(holderOwner);
    }

    public ResourceKey<R> key() {
        return key;
    }

    public final void tryBind(boolean throwOnMissingRegistry) {
        if (this.holder != null) {
            return;
        }

        final var registry = (Registry<R>) BuiltInRegistries.REGISTRY.get(this.key.registry());
        if (registry != null) {
            this.holder = (Holder<T>) registry.getHolder(this.key).orElse(null);
        } else if (throwOnMissingRegistry) {
            throw new IllegalStateException("Unknown registry '" + this.key.registry() + "'");
        }
    }
}
