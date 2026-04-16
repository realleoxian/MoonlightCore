package de.realleoxian.moonlightcore.forge.platform;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraftforge.eventbus.api.IEventBus;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ModEventBusHandler {
    private static final Map<String, IEventBus> MOD_EVENT_BUSES = new ConcurrentHashMap<>();
    private static final Table<String, Class<?>, Object> REGISTRATIONS = Tables.synchronizedTable(HashBasedTable.create());
    private static final Set<Object> REGISTERED_REGISTRATIONS = Sets.newConcurrentHashSet();

    public static @Nullable IEventBus getEventBus(String namespace) {
        return MOD_EVENT_BUSES.get(namespace);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getRegistration(String namespace, Class<T> clazz) {
        final var existing = REGISTRATIONS.get(namespace, clazz);
        if (existing == null) {
            try {
                T instance;
                try {
                    instance = clazz.getConstructor(String.class).newInstance(namespace);
                } catch (NoSuchMethodException e) {
                    instance = clazz.getConstructor().newInstance();
                }

                REGISTRATIONS.put(namespace, clazz, instance);
                final IEventBus eventBus = MOD_EVENT_BUSES.get(namespace);
                if (eventBus != null) registerToEventBus(eventBus, instance);

                return instance;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return (T) existing;
    }

    public static void register(String modId, IEventBus eventBus){
        MOD_EVENT_BUSES.put(modId, eventBus);

        synchronized (REGISTRATIONS) {
            for (final var registration : getByModId(modId)) {
                registerToEventBus(eventBus, registration);
            }
        }
    }

    private static <T> void registerToEventBus(IEventBus eventBus, T instance) {
        if (!REGISTERED_REGISTRATIONS.add(instance)) return;

        if (instance instanceof ModEventBusRegister register) {
            register.registerToEventBus(eventBus);
        } else {
            eventBus.register(instance);
        }
    }

    private static Collection<Object> getByModId(String namespace) {
        return REGISTRATIONS.row(namespace).values();
    }
}
