package de.leoxian.moonlightcore.common.event.base;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public final class Event<T> {
    public static <T> Event<T> create(Class<T> eventType, Function<T[], T> invokerFactory) {
        return new Event<>(eventType, invokerFactory);
    }

    private final ReentrantLock lock = new ReentrantLock();
    private final EnumMap<EventPriority, List<T>> listeners = new EnumMap<>(EventPriority.class);
    private final Class<T> eventType;
    private final Function<T[], T> invokerFactory;

    private volatile T invoker = null;
    private volatile boolean dirty = true;

    public Event(Class<T> eventType, Function<T[], T> invokerFactory) {
        this.eventType = eventType;
        this.invokerFactory = invokerFactory;

        for (final var priority : EventPriority.VALUES) {
            this.listeners.put(priority, new ArrayList<>());
        }
    }

    public T doFire() {
        if (this.dirty) {
            buildInvoker();
        }
        return this.invoker;
    }

    public void subscribe(EventPriority priority, T listener) {
        this.lock.lock();
        try {
            this.listeners.get(priority).add(listener);
            this.dirty = true;
        } finally {
            this.lock.unlock();
        }
    }

    public void subscribe(T listener) {
        subscribe(EventPriority.NORMAL, listener);
    }

    public void unsubscribe(T listener) {
        this.lock.lock();
        try {
            boolean wasRemoved = false;
            for (final var priority : EventPriority.VALUES) {
                wasRemoved |= this.listeners.get(priority).removeIf(t -> t == listener);
            }

            if (wasRemoved) {
                this.dirty = true;
            }
        } finally {
            this.lock.unlock();
        }
    }

    private void buildInvoker() {
        this.lock.lock();
        try {
            if (!this.dirty) return;

            var allListeners = new ArrayList<T>();
            for (final var priority : EventPriority.VALUES) {
                allListeners.addAll(this.listeners.get(priority));
            }

            @SuppressWarnings("unchecked")
            var listenersArray = allListeners.toArray((T[]) Array.newInstance(this.eventType, allListeners.size()));
            this.invoker = this.invokerFactory.apply(listenersArray);
            this.dirty = false;
        } finally {
            this.lock.unlock();
        }
    }
}
