package de.leoxian.moonlightcore.api.registry;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public interface RegistryHelper<R> {

    <T extends R> DeferredObject<R, T> register(String name, Function<ResourceLocation, T> func);

    default <T extends R> DeferredObject<R, T> register(String name, Supplier<T> sup) {
        return register(name, k -> sup.get());
    }

    @UnmodifiableView
    Set<DeferredObject<R, ?>> getDeferredObjects();

    String namespace();

}
