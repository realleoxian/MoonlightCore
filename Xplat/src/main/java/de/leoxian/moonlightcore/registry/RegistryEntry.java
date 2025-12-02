package de.leoxian.moonlightcore.registry;

import de.leoxian.moonlightcore.util.nullness.Nonnull;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RegistryEntry<R, T extends R> implements Supplier<@Nonnull T> {

    private final ResourceKey<R> key;

    private @Nullable Holder<R> holder;

    private @Nullable Supplier<? extends T> valueCandidate;
    private @Nullable T value = null;

    RegistryEntry(ResourceKey<R> key, Supplier<? extends T> valueCandidate) {
        this.key = key;
        this.valueCandidate = valueCandidate;
    }

    @Override
    public @Nonnull T get() {
        T ret = value;
        if(ret == null) {
            value = ret = valueCandidate.get();
            valueCandidate = null;

            if(value == null) {
                throw new IllegalStateException("Invalid registry entry value (" + this + ")");
            }
        }

        return ret;
    }

    public boolean is(ResourceLocation name) {
        return key.location() == name;
    }

    public boolean is(ResourceKey<R> key) {
        return key == this.key;
    }

    public boolean is(Predicate<ResourceKey<R>> filter) {
        return filter.test(key);
    }

    public boolean is(TagKey<R> tagKey) {
        bindReference();
        return holder != null && holder.is(tagKey);
    }

    public Stream<TagKey<R>> getTags() {
        bindReference();
        return holder != null ? holder.tags() : Stream.empty();
    }

    public Optional<Holder<R>> asHolder() {
        bindReference();
        return Optional.ofNullable(holder);
    }

    public ResourceLocation getName() {
        return key.location();
    }

    public ResourceKey<? extends Registry<R>> getRegistryType() {
        return ResourceKey.createRegistryKey(key.registry());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj.getClass() != this.getClass()) return false;

        RegistryEntry<?, ?> other = (RegistryEntry<?, ?>) obj;
        return other.key == key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "RegistryEntry[" + key + "]";
    }

    final void bindReference() {
        if(holder != null) {
            return;
        }

        @SuppressWarnings("unchecked")
        Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.get(key.registry());
        if(registry != null) {
            holder = registry.getHolder(key).orElse(null);
        }
    }
}
