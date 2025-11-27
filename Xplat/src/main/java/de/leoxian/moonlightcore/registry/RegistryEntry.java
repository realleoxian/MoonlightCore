package de.leoxian.moonlightcore.registry;

import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RegistryEntry<R, T extends R> implements NonnullSupplier<R> {
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
        updateReference(false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        updateReference(true);
        if(holder == null) {
            throw new IllegalStateException("Registry entry not present: " + this.key);
        }

        return (T) holder.value();
    }

    public boolean is(ResourceLocation name) {
        return name.equals(this.key.location());
    }

    public boolean is(ResourceKey<R> key) {
        return this.key == key;
    }

    public boolean is(TagKey<R> tagKey) {
        updateReference(false);
        return holder != null && holder.is(tagKey);
    }

    public boolean is(Predicate<ResourceKey<R>> filter) {
        this.updateReference(false);
        return holder != null && holder.is(filter);
    }

    public Stream<TagKey<R>> tags() {
        updateReference(false);
        return holder == null ? Stream.empty() : holder.tags();
    }

    public ResourceLocation getName() {
        return key.location();
    }

    public ResourceKey<? extends Registry<R>> getRegistryType() {
        return ResourceKey.createRegistryKey(this.key.registry());
    }

    public Optional<Holder<R>> asHolder() {
        return Optional.ofNullable(this.holder);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj.getClass() != this.getClass()) return false;

        RegistryEntry<?, ?> other = (RegistryEntry<?, ?>) obj;
        return this.key == other.key;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public String toString() {
        return "RegistryEntry[" + this.key + "]";
    }

    @SuppressWarnings("unchecked")
    protected @Nullable Registry<R> getRegistry() {
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(this.key.registry());
    }

    protected final void updateReference(boolean throwOnMissingRegistry) {
        if(this.holder != null) {
            return;
        }

        Registry<R> registry = getRegistry();
        if(registry != null) {
            this.holder = registry.getHolder(this.key).orElse(null);
        } else if (throwOnMissingRegistry) {
            String errorMessage = String.format(
                    "Registry not present for %s: %s",
                    this.key,
                    this.getRegistryType().location()
            );
            throw new IllegalStateException(errorMessage);
        }
    }

}
