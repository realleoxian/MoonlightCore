package de.leoxian.moonlightcore.registry;

import com.mojang.datafixers.util.Either;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RegistryEntry<R, T extends R> implements Holder<R> {

    public static <R, T extends R>  RegistryEntry<R, T> create(ResourceLocation registryKey, ResourceLocation id, Supplier<T> valueCandidate) {
        return create(ResourceKey.createRegistryKey(registryKey), id,  valueCandidate);
    }

    public static <R, T extends R> RegistryEntry<R, T> create(ResourceKey<? extends Registry<R>> registry, ResourceLocation id, Supplier<T> valueCandidate) {
        return create(ResourceKey.create(registry, id), valueCandidate);
    }

    public static <R, T extends R> RegistryEntry<R, T> create(ResourceKey<R> key, Supplier<T> valueCandidate) {
        return new RegistryEntry<>(key, valueCandidate);
    }

    private final ResourceKey<R> key;
    private final Supplier<T> valueCandidate;

    @Nullable
    private T cachedValue;
    @Nullable
    private Holder<R> innerHolder;

    private RegistryEntry(ResourceKey<R> key, Supplier<T> valueCandidate) {
        this.key = key;
        this.valueCandidate = valueCandidate;
    }

    public T get() {
        return (T) this.value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public R value() {
        this.bind(true);

        if(this.cachedValue == null) {
            if(this.innerHolder != null) {
                this.cachedValue = (T) this.innerHolder.value();
            } else {
                this.cachedValue = this.valueCandidate.get();
            }

            if(this.cachedValue == null) {
                throw new IllegalStateException("Invalid value cache for registry entry");
            }
        }

        return this.cachedValue;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public Registry<R> registry() {
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(this.key.registry());
    }

    public ResourceLocation id() {
        return this.key.location();
    }

    public ResourceLocation registryId() {
        return this.key.registry();
    }

    @Override
    public boolean isBound() {
        this.bind(false);
        return this.innerHolder != null && this.innerHolder.isBound();
    }

    @Override
    public boolean is(ResourceLocation resourceLocation) {
        return this.key.location().equals(resourceLocation);
    }

    @Override
    public boolean is(ResourceKey<R> resourceKey) {
        return resourceKey == this.key;
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return predicate.test(this.key);
    }

    @Override
    public boolean is(TagKey<R> tagKey) {
        this.bind(false);
        return this.innerHolder != null && this.innerHolder.is(tagKey);
    }

    @Override
    public Stream<TagKey<R>> tags() {
        this.bind(false);
        return this.innerHolder != null ? this.innerHolder.tags() : Stream.empty();
    }

    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return Either.right(this.cachedValue);
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
        this.bind(false);
        return this.innerHolder != null && this.innerHolder.canSerializeIn(holderOwner);
    }

    @Override
    public String toString() {
        return "RegistryEntry{id=" + this.key.location() + ", registry=" + this.key.registry() + "}";
    }

    private void bind(boolean throwIfMissingRegistry) {
        if(this.innerHolder != null) {
            return;
        }

        Registry<R> registry = this.registry();

        if(registry != null) {
            this.innerHolder = registry.getHolder(this.key).orElse(null);
        } else if (throwIfMissingRegistry) {
            throw new IllegalStateException("Registry not present for " + this + ": " + this.key.registry());
        }
    }

}
