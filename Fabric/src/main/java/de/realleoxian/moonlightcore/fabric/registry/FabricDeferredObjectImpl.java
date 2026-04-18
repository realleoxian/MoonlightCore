package de.realleoxian.moonlightcore.fabric.registry;

import com.mojang.logging.LogUtils;
import de.realleoxian.moonlightcore.api.registry.DeferredObject;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.slf4j.Logger;

import java.util.function.Predicate;

public class FabricDeferredObjectImpl<R, T extends R> implements DeferredObject<R, T> {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final ResourceKey<R> registryKey;
    private final T value;

    private @Nullable Holder<R> holder = null;

    FabricDeferredObjectImpl(ResourceKey<R> registryKey, T value) {
        this.registryKey = registryKey;
        this.value = value;
    }

    @Override
    public T get() {
        bind();
        return this.value;
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> filter) {
        return filter.test(this.registryKey);
    }

    @Override
    public boolean is(ResourceKey<R> key) {
        return this.registryKey == key;
    }

    @Override
    public boolean is(TagKey<R> tag) {
        bind();
        return this.holder != null && this.holder.is(tag);
    }

    @Override
    public boolean isBound() {
        return true;
    }

    @Override
    public @Nullable Holder<R> asHolder() {
        bind();
        return this.holder;
    }

    @Override
    public ResourceLocation name() {
        return this.registryKey.location();
    }

    @Override
    public ResourceKey<? extends Registry<R>> registryKey() {
        return ResourceKey.createRegistryKey(this.registryKey.registry());
    }

    private void bind() {
        if (this.holder != null) {
            return;
        }

        @SuppressWarnings("unchecked")
        Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.get(registryKey().location());
        if (registry != null) {
            this.holder = registry.getHolder(this.registryKey).orElse(null);

            if (this.holder == null) {
                LOGGER.warn("Couldn't bind registry holder: {}", this.registryKey.location());
            }
        } else {
            LOGGER.warn("Unknown registry: {}", this.registryKey().location());
        }
    }
}
