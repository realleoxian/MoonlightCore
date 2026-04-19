package de.realleoxian.moonlightcore.forge.registry;

import de.realleoxian.moonlightcore.api.registry.DeferredObject;
import de.realleoxian.moonlightcore.api.registry.RegistryHelper;
import de.realleoxian.moonlightcore.forge.platform.ModEventBusRegister;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ForgeRegistryManager implements RegistryHelper, ModEventBusRegister {
    private final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> registers = new HashMap<>();

    private final String namespace;

    public ForgeRegistryManager(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void registerToEventBus(IEventBus eventBus) {
        this.registers.values().forEach((register) -> register.register(eventBus));
    }

    @Override
    public <R> RegistryHelper.Registrar<R> registrar(ResourceKey<? extends Registry<R>> registryType) {
        return new ForgeRegistrar<>(registryType);
    }

    @Override
    public <R> Supplier<Registry<R>> getRegistry(ResourceKey<? extends Registry<R>> registryType) {
        @SuppressWarnings("unchecked")
        ResourceKey<Registry<R>> key = (ResourceKey<Registry<R>>) registryType;

        return () -> {
            @SuppressWarnings("unchecked")
            Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.get(key.location());
            if (registry == null) {
                throw new IllegalStateException("Registry not found: " + key.location());
            }
            return registry;
        };
    }

    private class ForgeRegistrar<R> implements RegistryHelper.Registrar<R> {
        private final ResourceKey<? extends Registry<R>> registryKey;
        private final DeferredRegister<R> wrapped;

        @SuppressWarnings("unchecked")
        private ForgeRegistrar(ResourceKey<? extends Registry<R>> registryKey) {
            this.registryKey = registryKey;
            this.wrapped = (DeferredRegister<R>) registers.computeIfAbsent(registryKey, k -> DeferredRegister.create(registryKey, namespace));
        }

        @Override
        public <T extends R> DeferredObject<R, T> register(String name, Function<ResourceLocation, T> func) {
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(namespace, name);
            ResourceKey<R> key = ResourceKey.create(this.registryKey, location);
            return new ForgeDeferredObject<>(key, this.wrapped.register(name, () -> func.apply(location)));
        }
    }
}
