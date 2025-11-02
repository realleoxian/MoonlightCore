package de.leoxian.moonlightcore.registry;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface LookupProvider {

    <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> registryKey);

}
