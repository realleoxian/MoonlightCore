package de.leoxian.moonlightcore.registry;

import de.leoxian.moonlightcore.event.common.RegisterEvent;
import de.leoxian.moonlightcore.platform.PlatformEnvironment;
import de.leoxian.moonlightcore.util.nullness.Nonnull;
import de.leoxian.moonlightcore.util.nullness.NonnullConsumer;
import de.leoxian.moonlightcore.util.nullness.NonnullSupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;

public class DeferredRegistrar<R> {
    public static <R> DeferredRegistrar<R> create(Registry<R> registryType, String modId) {
        return create(registryType.key(), modId);
    }

    public static <R> DeferredRegistrar<R> create(ResourceKey<? extends Registry<R>> registryType, String modId) {
        return new DeferredRegistrar<>(registryType, modId);
    }

    private final Map<String, Registration<?>> registrations = new HashMap<>();
    private final Map<String, List<NonnullConsumer<? extends R>>> registerCallbacks = new HashMap<>();

    private final ResourceKey<? extends Registry<R>> registryType;
    private final String modId;
    private final Logger logger;

    private DeferredRegistrar(ResourceKey<? extends Registry<R>> registryType, String modId) {
        this.registryType = registryType;
        this.modId = modId;
        this.logger = LoggerFactory.getLogger("MoonlightCore | DeferredRegistrar/" + modId);
    }

    public void bind() {
        if(!this.registerCallbacks.isEmpty()) {
            this.registerCallbacks.forEach((k, v) -> logger.warn("Found {} unused register callbacks for entry {} [{}]. Was the entry ever registered?", v.size(), k, this.registryType.location()));
            this.registerCallbacks.clear();

            if(PlatformEnvironment.INSTANCE.isDevelopmentEnvironment()) {
                throw new IllegalStateException("Found unused register callbacks, see logs");
            }
        }

        if(!this.registerCallbacks.isEmpty()) {
            logger.info("Registering {} known objects of type {}", registrations.size(), registryType.location());

            RegisterEvent.EVENT.subscribe((currentRegistry, output) -> {
                if(currentRegistry == registryType) {
                    for(Registration<?> registration : registrations.values()) {

                        try {
                            registration.accept(currentRegistry, output);
                            logger.info("Registered {} to registry {}", registration.name, registryType.location());
                        } catch (Exception e) {
                            String errorMessage = String.format(
                                    "Unexpected error while registering entry %s to registry %s",
                                    registration.name,
                                    registryType.location()
                            );

                            throw new RuntimeException(errorMessage);
                        }
                    }
                }
            });
        }
    }

    public <T extends R> RegistryEntry<R, T> register(String name, NonnullSupplier<T> factory) {
        ResourceLocation fullName = new ResourceLocation(modId, name);
        if(this.registrations.containsKey(name)) {
            throw new IllegalStateException("Duplicated entry name. \n  - Registry: %s\n  - Entry name: %s".formatted(this.registryType.location(), fullName));
        }

        Registration<T> registration = new Registration<>(fullName, factory);

        if(this.registerCallbacks.containsKey(name)) {
            this.registerCallbacks.remove(name).forEach(callback -> {
                @SuppressWarnings("unchecked")
                @Nonnull NonnullConsumer<? super T> unsafeCallback = (NonnullConsumer<? super T>) callback;
                registration.addCallback(unsafeCallback);
            });
        }

        this.registrations.put(name, registration);
        return registration.delegate;
    }

    private class Registration<T extends R> implements BiConsumer<ResourceKey<? extends Registry<?>>, RegisterEvent.Output> {
        ResourceLocation name;
        NonnullSupplier<? super T> factory;

        RegistryEntry<R, T> delegate;

        List<NonnullConsumer<? super T>> callbacks = new ArrayList<>();

        private Registration(ResourceLocation name, NonnullSupplier<? super T> factory) {
            this.name = name;
            this.factory = factory.lazy();

            this.delegate = RegistryEntry.create(registryType, name);
        }

        @Override
        public void accept(ResourceKey<? extends Registry<?>> resourceKey, RegisterEvent.Output output) {
            if(resourceKey == registryType) {
                output.register(name, factory);
                delegate.bindReference(false);
            }
        }

        void addCallback(NonnullConsumer<? super T> callback) {
            Objects.requireNonNull(callback, "Callback may not be null");
            this.callbacks.add(callback);
        }
    }
}
