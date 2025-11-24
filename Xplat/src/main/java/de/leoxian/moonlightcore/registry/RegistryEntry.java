package de.leoxian.moonlightcore.registry;

import com.mojang.datafixers.util.Either;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RegistryEntry<R, T extends R> implements Holder<R> {
    public static <R, T extends R> RegistryEntry<R, T> create(ResourceKey<? extends Registry<R>> registryType, ResourceLocation name) {
        return create(ResourceKey.create(registryType, name));
    }

    public static <R, T extends R> RegistryEntry<R, T> create(ResourceLocation registryName, ResourceLocation name) {
        return create(ResourceKey.createRegistryKey(registryName), name);
    }

    public static <R, T extends R> RegistryEntry<R, T> create(ResourceKey<R> key) {
        return new RegistryEntry<>(key);
    }

    private final ResourceKey<R> key;
    private @Nullable Holder<R> holder = null;

    protected RegistryEntry(ResourceKey<R> key) {
        this.key = Objects.requireNonNull(key);
        this.bindReference(false);
    }

    @Override
    public R value() {
        this.bindReference(true);
        return this.holder.value();
    }

    @Override
    public boolean isBound() {
        this.bindReference(false);
        return this.holder != null && this.holder.isBound();
    }

    @Override
    public boolean is(ResourceLocation resourceLocation) {
        return resourceLocation.equals(this.key.location());
    }

    @Override
    public boolean is(ResourceKey<R> resourceKey) {
        return this.key == resourceKey;
    }

    @Override
    public boolean is(TagKey<R> tagKey) {
        this.bindReference(false);
        return this.holder != null && this.holder.is(tagKey);
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return predicate.test(this.key);
    }

    @Override
    public Stream<TagKey<R>> tags() {
        this.bindReference(false);
        return this.holder != null ? this.holder.tags() : Stream.empty();
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
    public boolean canSerializeIn(HolderOwner<R> holderOwner) {
        this.bindReference(false);
        return this.holder != null && this.holder.canSerializeIn(holderOwner);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        return obj instanceof Holder<?> h && h.kind() == Kind.REFERENCE && ((Holder.Reference<?>) h).key() == key;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "DeferredHolder[%s]", this.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    public ResourceLocation getName() {
        return this.key.location();
    }

    public ResourceKey<R> getKey() {
        return key;
    }

    @SuppressWarnings("unchecked")
    protected @Nullable Registry<R> getRegistry() {
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(this.key.registry());
    }

    protected final void bindReference(boolean throwOnMissingRegistry) {
        if(this.holder != null) {
            return;
        }

        Registry<R> registry = getRegistry();
        if(registry != null) {
            this.holder = registry.getHolder(this.key).orElse(null);
        } else if (throwOnMissingRegistry) {
            throw new IllegalStateException("Registry not present for " + this + ": " + this.key.registry());
        }
    }
}
