package de.leoxian.moonlightcore.event.common;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.event.Event;
import de.leoxian.moonlightcore.event.EventFactory;
import de.leoxian.moonlightcore.registry.DatapackRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface DatapackRegistryCreationEvent {
    Event<DatapackRegistryCreationEvent> EVENT = EventFactory.create(DatapackRegistryCreationEvent.class);

    /**
     * Invoked to register custom datapack registries, these registries may be read-only if no codec is provided
     * @param output The output of the event, used to register the registries
     */
    void onDatapackRegistryCreation(DatapackRegistryCreationEvent.Output output);

    interface Output {
        /**
         * Register a new, custom registry
         * @param key The {@link ResourceKey} of the registry
         * @param codec The codec used to de/serialize the registry
         * @param priority The priority of the bootstrap function. The higher the priority, the earlier the bootstrap
         * @param bootstrap The bootstrap function, which is called when the registry is loaded from a datapack
         * @param <T> The type of the registry elements
         */
        <T> void register(ResourceKey<? extends Registry<T>> key, Codec<T> codec, int priority, Consumer<BootstapContext<T>> bootstrap);

        <T> void register(ResourceKey<? extends Registry<T>> key, int priority, Consumer<BootstapContext<T>> bootstrap);

        boolean isRegistered(ResourceLocation registryID);

        /**
         * Register a new custom registry
         * @param key The {@link ResourceKey} of the registry
         * @param codec The {@link Codec} used to de/serialize the registry
         * @param bootstrap The bootstrap function, which is called when the registry is loaded from a datapack
         * @param <T> The type of the registry elements
         */
        default <T> void register(ResourceKey<? extends Registry<T>> key, Codec<T> codec, Consumer<BootstapContext<T>> bootstrap) {
            register(key, codec, DatapackRegistryBuilder.DEFAULT_PRIORITY, bootstrap);
        }

        /**
         * Add a bootstrap function that will be called whenever the registry is loaded from a datapack
         * @param key The {@link ResourceKey} of the registry
         * @param bootstrap The boostrap function that will be called when the registry is loaded from a datapack
         * @param <T> The type of the registry elements
         */
        default <T> void addBootstrap(ResourceKey<? extends Registry<T>> key, Consumer<BootstapContext<T>> bootstrap) {
            register(key, DatapackRegistryBuilder.DEFAULT_PRIORITY, bootstrap);
        }

        /**
         * Add a bootstrap function that will be called whenever the registry is loaded from a datapack
         * @param key The {@link ResourceKey} of the registry
         * @param priority The priority of the bootstrap function. The higher the priority, the earlier the bootstrap
         * @param bootstrap The bootstrap function that will be called when the registry is loaded from a datapack
         * @param <T> The type of registry elements
         */
        default <T> void addReadOnlyBootstrap(ResourceKey<? extends Registry<T>> key, int priority, Consumer<BootstapContext<T>> bootstrap) {
            register(key, Math.min(DatapackRegistryBuilder.MAX_READONLY_PRIORITY, Integer.MIN_VALUE + priority), bootstrap);
        }

        /**
         * Add a bootstrap function that will be called whenever the registry is loaded from a datapack
         * @param key The {@link ResourceKey} of the registry
         * @param bootstrap The bootstrap function that will be called when the registry is loaded from a datapack
         * @param <T> The type of registry elements
         */
        default <T> void addReadOnlyBootstrap(ResourceKey<? extends Registry<T>> key, Consumer<BootstapContext<T>> bootstrap) {
            register(key, DatapackRegistryBuilder.MAX_READONLY_PRIORITY, bootstrap);
        }
    }
}
