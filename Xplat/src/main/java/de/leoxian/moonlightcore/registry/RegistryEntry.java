package de.leoxian.moonlightcore.registry;

import de.leoxian.moonlightcore.util.nullness.Nonnull;
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

public class RegistryEntry<R, T extends R> implements NonnullSupplier<T> {

    public static <R, T extends R> RegistryEntry<R, T> create(ResourceLocation registryType, ResourceLocation name) {
        return create(ResourceKey.create(ResourceKey.createRegistryKey(registryType), name));
    }

    public static <R, T extends R> RegistryEntry<R, T> create(ResourceKey<? extends Registry<R>> registryType, ResourceLocation name) {
        return create(ResourceKey.create(registryType, name));
    }

    public static <R, T extends R> RegistryEntry<R, T> create(ResourceKey<R> key) {
        return new RegistryEntry<>(key);
    }

    private final ResourceKey<R> key;
    private final ResourceKey<? extends Registry<R>> registryType;

    private @Nullable Holder<R> holder = null;
    private @Nullable T value = null;

    protected RegistryEntry(ResourceKey<R> key) {
        this.key = Objects.requireNonNull(key, "Registry entry key cannot be null");
        this.registryType = ResourceKey.createRegistryKey(key.registry());
        updateReference(false);
    }

    @Override
    public @Nonnull T get() {
        updateReference(true);

        T ret = this.value;
        Objects.requireNonNull(ret, () -> "Registry entry not present: " + this);
        return ret;
    }

    public boolean is(ResourceLocation name) {
        return getName() == name;
    }

    public boolean is(ResourceKey<R> key) {
        return key == this.key;
    }

    public boolean is(Predicate<ResourceKey<R>> filter) {
        return filter.test(key);
    }

    public boolean is(TagKey<R> tagKey) {
        updateReference(false);
        return holder != null && holder.is(tagKey);
    }

    public Stream<TagKey<R>> getTags() {
        updateReference(false);
        return holder != null ? holder.tags() : Stream.empty();
    }

    public Optional<Holder<R>> asHolder() {
        return Optional.ofNullable(holder);
    }

    public ResourceKey<R> getKey() {
        return key;
    }

    public ResourceLocation getName() {
        return key.location();
    }

    public ResourceKey<? extends Registry<R>> getRegistryType() {
        return registryType;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj.getClass() != this.getClass()) return false;

        RegistryEntry<?, ?> other = (RegistryEntry<?, ?>) obj;
        return other.key == key;
    }

    @Override
    public String toString() {
        return "RegistryEntry[" + key + "]";
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @SuppressWarnings("unchecked")
    Registry<R> getRegistry() {
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(key.registry());
    }

    @SuppressWarnings("unchecked")
    final void updateReference(boolean throwOnMissingRegistry) {
        if(holder != null && value != null) {
            return;
        }

        Registry<R> registry = getRegistry();
        if(registry != null) {
            holder = registry.getHolder(key).orElse(null);
            value = (T) registry.getOptional(key).orElse(null);
        } else if (throwOnMissingRegistry) {
            String errorMessage = String.format("Registry not present for %s: %s", key.location(), key.registry());
            throw new IllegalStateException(errorMessage);
        }
    }
}
