package de.leoxian.moonlightcore.common.registry;

import de.leoxian.moonlightcore.common.event.NewRegistryEvent;
import de.leoxian.moonlightcore.common.event.RegisterEvent;
import de.leoxian.moonlightcore.common.event.base.EventPriority;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class DeferredRegistrar<R> {
    public static <R> DeferredRegistrar<R> create(ResourceKey<? extends Registry<R>> registryKey, String namespace) {
        return new DeferredRegistrar<>(registryKey, namespace);
    }

    public static <R> DeferredRegistrar<R> create(Registry<R> registry, String namespace) {
        return new DeferredRegistrar<>(registry.key(), namespace);
    }

    private final Map<DeferredHolder<R, ?>, Supplier<R>> registration = new LinkedHashMap<>();
    private final Set<DeferredHolder<R, ?>> registrationView = Collections.unmodifiableSet(this.registration.keySet());
    private final ResourceKey<? extends Registry<R>> registryKey;
    private final String modId;

    @Nullable
    private Registry<R> customRegistry = null;

    private boolean seenRegisterEvent = false;
    private boolean seenNewRegistryEvent = false;

    private DeferredRegistrar(ResourceKey<? extends Registry<R>> registryKey, String modId) {
        this.registryKey = registryKey;
        this.modId = modId;
        RegisterEvent.EVENT.subscribe(EventPriority.HIGHEST, this::onRegisterEvent);
        NewRegistryEvent.EVENT.subscribe(EventPriority.HIGHEST, this::onNewRegistryEvent);
    }

    public <T extends R> DeferredHolder<R, T> register(final String name, final Function<Identifier, T> func) {
        if (seenRegisterEvent) throw new IllegalStateException("Cannot register new entries after RegisterEvent was invoked");
        Objects.requireNonNull(name, "Name may not be 'null'");
        Objects.requireNonNull(func, "Func may not be 'null'");

        final Identifier id = Identifier.fromNamespaceAndPath(this.modId, name);
        var ret = DeferredHolder.<R, T>create(this.registryKey, id);
        if (this.registration.putIfAbsent(ret, () -> func.apply(id)) != null) {
            throw new IllegalArgumentException("Duplicated registration: " + name);
        }
        return ret;
    }

    public <T extends R> DeferredHolder<R, T> register(final String name, Supplier<T> sup) {
        return register(name, k -> sup.get());
    }

    public Registry<R> makeRegistry(final Consumer<RegistryBuilder<R>> consumer) {
        return makeRegistry(this.registryKey.identifier(), consumer);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public Registry<R> getRegistry() {
        if (this.customRegistry == null) {
            this.customRegistry = (Registry<R>) BuiltInRegistries.REGISTRY.get(this.registryKey.identifier()).orElse(null);
        }
        return this.customRegistry;
    }

    public Collection<DeferredHolder<R, ? extends R>> getEntries() {
        return this.registrationView;
    }

    private Registry<R> makeRegistry(final Identifier registryId, final Consumer<RegistryBuilder<R>> consumer) {
        if (registryId == null)
            throw new IllegalArgumentException("May registry's id not be 'null'");
        if (BuiltInRegistries.REGISTRY.containsKey(registryId) || this.customRegistry != null)
            throw new IllegalStateException("Cannot create a registry that already exists: " + this.registryKey);
        if (this.seenNewRegistryEvent)
            throw new IllegalStateException("Cannot create a new registry after NewRegistryEvent was invoked");

        var builder = RegistryBuilder.of(this.registryKey);
        consumer.accept(builder);
        this.customRegistry = builder.build();
        return this.customRegistry;
    }

    private void onRegisterEvent(ResourceKey<? extends Registry<?>> registryKey, RegisterEvent.Output output) {
        if (registryKey == this.registryKey) {
            this.seenRegisterEvent = true;
            this.registration.forEach((holder, sup) -> output.register(holder.getKey().identifier(), sup));
        }
    }

    private void onNewRegistryEvent(Consumer<Registry<?>> output) {
        this.seenNewRegistryEvent = true;
        if (this.customRegistry != null) {
            output.accept(this.customRegistry);
        }
    }
}
