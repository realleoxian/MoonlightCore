package de.realleoxian.moonlightcore.xplat.event;

import de.realleoxian.moonlightcore.api.event.CancellableEvent;
import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import de.realleoxian.moonlightcore.api.event.EventPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public final class EventImpl<T extends EventBase> implements Event<T> {
    private static final EventPriority[] PRIORITIES = EventPriority.values();

    private final AtomicReference<Consumer<T>> invoker = new AtomicReference<>($ -> {});
    private final ReentrantLock lock = new ReentrantLock();

    private final EnumMap<EventPriority, List<Consumer<T>>> listeners = new EnumMap<>(EventPriority.class);

    private final String eventTypeName;
    private final Logger eventLogger;

    private volatile boolean dirty = true;

    public EventImpl(Class<T> eventType) {
        this.eventTypeName = eventType.getName().replace('.', '/');
        this.eventLogger = LoggerFactory.getLogger("moonlightcore:event/" + this.eventTypeName);

        for (final var priority : PRIORITIES) {
            this.listeners.put(priority, new ArrayList<>());
        }
    }

    @Override
    public T doFire(T event) {
        getInvoker().accept(event);
        return event;
    }

    @Override
    public void subscribe(EventPriority priority, Consumer<T> listener) {
        this.lock.lock();
        try {
            this.listeners.get(priority).add(listener);
            this.dirty = true;
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void unsubscribe(Consumer<T> listener) {
        this.lock.lock();
        try {
            boolean removed = false;
            for (final var list : this.listeners.values()) {
                removed |= list.remove(listener);
            }

            if (removed) {
                this.dirty = true;
            }
        } finally {
            this.lock.unlock();
        }
    }

    private Consumer<T> getInvoker() {
        if (this.dirty) buildInvoker();
        return this.invoker.get();
    }

    private void buildInvoker() {
        this.lock.lock();
        try {
            if (!this.dirty) {
                return;
            }

            var snapshot = new ArrayList<Consumer<T>>();
            for (final var priority : PRIORITIES) {
                snapshot.addAll(this.listeners.get(priority));
            }

            if (snapshot.isEmpty()) {
                this.invoker.set($ -> {});
                this.dirty = false;
                return;
            }

            var invoker = (Consumer<T>) event -> {
                for (int i = 0; i < snapshot.size(); i++) {
                    try {
                        snapshot.get(i).accept(event);

                        if (event instanceof CancellableEvent cancellable && cancellable.isCancelled()) {
                            break;
                        }
                    } catch (Throwable throwable) {
                        String errorMessage = """
                                Caught an error invoking event listeners for '%s' event.
                                - Error message: '%s'
                                - Listener index: %d
                                """.formatted(this.eventTypeName, throwable.getMessage(), i);
                        this.eventLogger.error(errorMessage);
                    }
                }
            };
            this.invoker.set(invoker);
            this.dirty = false;
        } finally {
            this.lock.unlock();
        }
    }
}
