package de.leoxian.moonlightcore.neoforge.common;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.neoforged.bus.api.IEventBus;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public final class ModEventBuses {
    private static final Map<String, IEventBus> EVENT_BUSES = new HashMap<>();
    private static final Map<String, Set<Object>> DEFERRED_REGISTERED_EVENTS = new HashMap<>();
    private static final Table<String, Class<?>, Object> EVENT_LISTENERS = HashBasedTable.create();

    public static synchronized void registerEventBus(String modId, IEventBus eventBus) {
        if (EVENT_BUSES.putIfAbsent(modId, eventBus) != null) {
            throw new IllegalStateException("May not register a duplicated event bus with mod id: " + modId);
        }

        Set<Object> deferred = DEFERRED_REGISTERED_EVENTS.remove(modId);
        if (deferred != null) {
            for (Object listener : deferred) {
                if (listener instanceof ModEventBusRegistrable modEventBusRegistrable) {
                    modEventBusRegistrable.register(eventBus);
                } else {
                    eventBus.register(listener);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static synchronized <T> T registerListener(String modId, Class<T> type) {
        Object existing = EVENT_LISTENERS.get(modId, type);
        if (existing != null) {
            return (T) existing;
        } else {
            try {
                Object instance;
                try {
                    instance = type.getConstructor(String.class).newInstance(modId);
                } catch (NoSuchMethodException e) {
                    instance = type.getConstructor().newInstance();
                }

                EVENT_LISTENERS.put(modId, type, instance);
                IEventBus eventBus = EVENT_BUSES.get(modId);
                if (eventBus == null) {
                    DEFERRED_REGISTERED_EVENTS.computeIfAbsent(modId, k -> new HashSet<>()).add(instance);
                } else {
                    if (instance instanceof ModEventBusRegistrable modEventBusRegistrable) {
                        modEventBusRegistrable.register(eventBus);
                    } else {
                        eventBus.register(instance);
                    }
                }
                return (T) instance;
            }  catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static Optional<IEventBus> getBus(String modId) {
        return Optional.ofNullable(EVENT_BUSES.get(modId));
    }

    private ModEventBuses() {}
}
