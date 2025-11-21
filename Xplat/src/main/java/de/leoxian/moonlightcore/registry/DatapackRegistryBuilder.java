package de.leoxian.moonlightcore.registry;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import de.leoxian.moonlightcore.event.common.DatapackRegistryCreationEvent;
import de.leoxian.moonlightcore.util.PriorityLinkedList;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.*;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@ApiStatus.Internal
public final class DatapackRegistryBuilder {
    private static final PriorityLinkedList<Entry<?>> REGISTRIES = new PriorityLinkedList<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean seenRegistryEvent = false;

    public static final int DEFAULT_PRIORITY = 1000;
    public static final int MAX_READONLY_PRIORITY = -1000;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <E> void bootstrap(RegistryOps.RegistryInfoLookup infoLookup, ResourceKey<? extends Registry<E>> key, WritableRegistry<E> writableRegistry) {
        dispatchEvent();
        LOGGER.debug("Bootstrapping registry: {}", key.location());

        REGISTRIES.forEach(entry -> {
            if(entry.key.equals(key)) {
                LOGGER.debug("Invoking registry bootstrap for '{}'", key.location());
                entry.bootstrap().accept(entry.getContext(infoLookup, (WritableRegistry) writableRegistry));
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static void bootstrap(BiConsumer<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryBootstrap<?>> consumer) {
        REGISTRIES.forEach(entry ->
                consumer.accept(entry.key, (ctx) ->
                        entry.bootstrap().accept((BootstapContext) ctx)));
    }

    public static void forEach(BiConsumer<ResourceKey<? extends Registry<?>>, @Nullable Codec<?>> output) {
        REGISTRIES.forEach(entry ->
                output.accept(entry.key, entry.codec));
    }

    private static void dispatchEvent() {
        if(seenRegistryEvent) {
            return;
        }
        seenRegistryEvent = true;

        DatapackRegistryCreationEvent.Output output = new DatapackRegistryCreationEvent.Output() {
            @Override
            public <T> void register(ResourceKey<? extends Registry<T>> key, Codec<T> codec, int priority, Consumer<BootstapContext<T>> bootstrap) {
                if(isRegistered(key.location())) {
                    throw new IllegalStateException("Encountered duplicated datapack registry registration: " + key.location());
                }

                LOGGER.info("Registering datapack registry: {}", key.location());
                REGISTRIES.add(new Entry<>(key, codec, bootstrap), priority);
            }

            @Override
            public <T> void register(ResourceKey<? extends Registry<T>> key, int priority, Consumer<BootstapContext<T>> bootstrap) {
                LOGGER.info("Registering datapack registry bootstrap to: {}", key.location());
                REGISTRIES.add(new Entry<>(key, null, bootstrap), priority);
            }

            @Override
            public boolean isRegistered(ResourceLocation registryID) {
                return REGISTRIES.stream().filter(Entry::definesRegistry).anyMatch(entry -> entry.key().location().equals(registryID));
            }
        };

        DatapackRegistryCreationEvent.EVENT.invoker().onDatapackRegistryCreation(output);
    }

    private static <T> BootstapContext<T> makeContext(RegistryOps.RegistryInfoLookup infoLookup, WritableRegistry<T> registry) {
        return new BootstapContext<T>() {
            @Override
            public Holder.Reference<T> register(ResourceKey<T> resourceKey, T t, Lifecycle lifecycle) {
                if(!registry.containsKey(resourceKey)) {
                    return registry.register(resourceKey, t, lifecycle);
                } else {
                    return registry.getHolderOrThrow(resourceKey);
                }
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
                return infoLookup.lookup(resourceKey).map(o -> o.getter()).orElse(null);
            }
        };
    }

    private DatapackRegistryBuilder() {}

    private record Entry<T>(ResourceKey<? extends Registry<T>> key, @org.jetbrains.annotations.Nullable Codec<T> codec, Consumer<BootstapContext<T>> bootstrap) {

        public BootstapContext<T> getContext(RegistryOps.RegistryInfoLookup infoLookup, WritableRegistry<T> writableRegistry) {
            return DatapackRegistryBuilder.makeContext(infoLookup, writableRegistry);
        }

        public boolean definesRegistry() {
            return this.codec() != null;
        }

    }
}
