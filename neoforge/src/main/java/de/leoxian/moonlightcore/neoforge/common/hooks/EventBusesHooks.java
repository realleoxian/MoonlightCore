package de.leoxian.moonlightcore.neoforge.common.hooks;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.*;
import java.util.function.Consumer;

public final class EventBusesHooks {
    private static final Table<String, Class<?>, Object> LISTENERS = HashBasedTable.create();
    private static final Table<ResourceKey<? extends Registry<?>>, String, DeferredRegister<?>> REGISTERS = HashBasedTable.create();

    @SuppressWarnings("unchecked")
    public static <T> void atListener(final String modId, final Class<? extends T> listenerType, final Consumer<T> initializer) {
        synchronized (LISTENERS) {
            Object existing = LISTENERS.get(modId, listenerType);
            if (existing != null) {
                initializer.accept((T) existing);
                return;
            }

            T instance;
            try {
                try {
                    instance = listenerType.getConstructor(String.class).newInstance(modId);
                } catch (NoSuchMethodException _) {
                    instance = listenerType.getConstructor().newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            LISTENERS.put(modId, listenerType, instance);
            initializer.accept(instance);

            IEventBus modEventBus = getModEventBusOrThrow(modId);
            if (instance instanceof ModEventBusRegistrable registrable) {
                registrable.register(modEventBus);
            } else {
                modEventBus.register(instance);
            }
        }
    }

    public static <T> T getListener(final String modId, final Class<? extends T> listenerType) {
        synchronized (LISTENERS){
            Object existing = LISTENERS.get(modId, listenerType);
            if (existing != null) {
                return (T) existing;
            }

            T instance;
            try {
                try {
                    instance = listenerType.getConstructor(String.class).newInstance(modId);
                } catch (NoSuchMethodException _) {
                    instance = listenerType.getConstructor().newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            LISTENERS.put(modId, listenerType, instance);
            IEventBus modEventBus = getModEventBusOrThrow(modId);
            if (instance instanceof ModEventBusRegistrable registrable) {
                registrable.register(modEventBus);
            } else {
                modEventBus.register(instance);
            }
            return instance;
        }
    }

    public static void whenAvailable(final String modId, final Consumer<IEventBus> initializer) {
        IEventBus eventBus = getModEventBusOrThrow(modId);
        initializer.accept(eventBus);
    }

    @SuppressWarnings("unchecked")
    public static <R> DeferredRegister<R> getDeferredRegister(final ResourceKey<? extends Registry<R>> registryKey, final String modId) {
        DeferredRegister<?> existing = REGISTERS.get(registryKey, modId);
        if (existing != null) {
            return (DeferredRegister<R>) existing;
        }

        DeferredRegister<R> register = DeferredRegister.create(registryKey, modId);
        register.register(getModEventBusOrThrow(modId));
        REGISTERS.put(registryKey, modId, register);
        return register;
    }

    public static Optional<IEventBus> getModEventBus(final String modId) {
        return ModList.get().getModContainerById(modId)
                .map(ModContainer::getEventBus);
    }

    public static IEventBus getModEventBusOrThrow(final String modId) {
        return getModEventBus(modId).orElseThrow(() -> new IllegalStateException("Mod '" + modId + "' is not available"));
    }

    private EventBusesHooks() {}
}
