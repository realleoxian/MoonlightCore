package de.leoxian.moonlightcore.registry.builder;

import de.leoxian.moonlightcore.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface Builder<R, T extends R> {

    RegistryEntry<R, T> build(Consumer<RegistryEntry<R, ?>> output);

    ResourceKey<? extends Registry<R>> registryKey();

    ResourceLocation id();

}
