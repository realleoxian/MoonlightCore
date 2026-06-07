package de.realleoxian.moonlightcore.api.registry;

import de.realleoxian.moonlightcore.api.event.NewRegistryEvent;
import de.realleoxian.moonlightcore.api.event.RegisterEvent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class DeferredRegistrar<R> {
    public static <R> DeferredRegistrar<R> create(ResourceKey<? extends Registry<R>> key, String namespace) {
        return new DeferredRegistrar<>(key, namespace);
    }

    public static <R> DeferredRegistrar<R> create(Registry<R> registry, String namespace) {
        return new DeferredRegistrar<>(registry.key(), namespace);
    }

    private final Map<DeferredHolder<R, ?>, R> registration = new LinkedHashMap<>();
    private final Set<DeferredHolder<R, ?>> registrationView = Collections.unmodifiableSet(this.registration.keySet());
    private final ResourceKey<? extends Registry<R>> registryKey;
    private final String namespace;

    @Nullable
    private RegistryInformation customRegistryInformation = null;
    @Nullable
    private Registry<R> customRegistry = null;

    private volatile boolean newRegistryEventInvoked = false;
    private volatile boolean registerEventInvoked = false;

    private DeferredRegistrar(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
        this.registryKey = registryKey;
        this.namespace = namespace;
        RegisterEvent.EVENT.subscribe(this::onRegisterEvent);
        NewRegistryEvent.EVENT.subscribe(this::onNewRegistryEvent);
    }

    public <T extends R> DeferredHolder<R, T> register(String name, Function<ResourceLocation, T> func) {
        if (this.registerEventInvoked) throw new IllegalStateException("Cannot register new entries when RegisterEvent has been invoked");
        Objects.requireNonNull(name, "Entry's name may not be 'null'");
        Objects.requireNonNull(func, "Entry's value may not be 'null'");

        final var key = ResourceLocation.fromNamespaceAndPath(this.namespace, name);
        final var holder = new DeferredHolder<R, T>(ResourceKey.create(this.registryKey, key));
        if (this.registration.putIfAbsent(holder, func.apply(key)) != null) {
            throw new IllegalArgumentException("Found duplicated registry key '" + key + "' for registry '" + this.registryKey + "'");
        }
        return holder;
    }

    public <T extends R> DeferredHolder<R, T> register(String name, Supplier<T> sup) {
        return register(name, k -> sup.get());
    }

    public void defineRegistry(Consumer<RegistryInformation> informationModifier) {
        if (this.newRegistryEventInvoked) throw new IllegalStateException("Cannot define new registries when NewRegistryEvent has been invoked");
        final var information = RegistryInformation.create(this.registryKey);
        informationModifier.accept(information);
        this.customRegistryInformation = information;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public Registry<R> getRegistry() {
        if (this.customRegistry == null) {
            this.customRegistry = (Registry<R>) BuiltInRegistries.REGISTRY.get(this.registryKey.location());
        }
        return this.customRegistry;
    }

    @UnmodifiableView
    public Set<DeferredHolder<R, ? extends R>> getRegistrations() {
        return this.registrationView;
    }

    @ApiStatus.Internal
    private void onRegisterEvent(RegisterEvent registerEvent) {
        if (registerEvent.registryKey.equals(this.registryKey)) {
            this.registerEventInvoked = true;
            this.registration.forEach((holder, value) -> {
                registerEvent.register(holder.key().location(), value);
                holder.tryBind(false);
            });
        }
    }

    @ApiStatus.Internal
    private void onNewRegistryEvent(NewRegistryEvent event) {
        this.newRegistryEventInvoked = true;
        if (this.customRegistryInformation != null) {
            event.register(this.customRegistryInformation);
        }
    }
}
