package de.leoxian.moonlightcore.api.registry;

import de.leoxian.moonlightcore.api.datamap.DataMapHolder;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface DeferredObject<R, T extends R> extends Supplier<T>, DataMapHolder {

    T get();

    boolean is(Predicate<ResourceKey<R>> filter);

    boolean is(ResourceKey<R> key);

    boolean is(TagKey<R> tag);

    boolean isBound();

    @Nullable
    Holder<R> asHolder();

    ResourceLocation name();

    ResourceKey<? extends Registry<R>> registryKey();

}
