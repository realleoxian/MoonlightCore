package de.realleoxian.moonlightcore.fabric.registry;

import com.google.common.base.Suppliers;
import de.realleoxian.moonlightcore.api.registry.DeferredObject;
import de.realleoxian.moonlightcore.api.registry.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public record FabricRegistryHelperImpl(String namespace) implements RegistryHelper {
    @Override
    public <R> Registrar<R> createHelper(ResourceKey<? extends Registry<R>> registryType) {
        return new FabricRegistrar<>(registryType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Supplier<Registry<R>> getRegistry(ResourceKey<? extends Registry<R>> registryType) {
        return Suppliers.memoize(() -> (Registry<R>) Objects.requireNonNull(BuiltInRegistries.REGISTRY.get(registryType.location()), "Unknown registry: " + registryType.location()));
    }

    private class FabricRegistrar<R> implements Registrar<R> {
        private final ResourceKey<? extends Registry<R>> registryKey;

        private FabricRegistrar(ResourceKey<? extends Registry<R>> registryKey) {
            this.registryKey = registryKey;
        }

        @Override
        public <T extends R> DeferredObject<R, T> register(String name, Function<ResourceLocation, T> func) {
            ResourceLocation location = new ResourceLocation(namespace(), name);
            return new FabricDeferredObjectImpl<>(ResourceKey.create(this.registryKey, location), func.apply(location));
        }
    }
}
