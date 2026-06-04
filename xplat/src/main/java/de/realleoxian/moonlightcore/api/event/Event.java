package de.realleoxian.moonlightcore.api.event;

import de.realleoxian.moonlightcore.xplat.event.EventImpl;

import java.util.function.Consumer;

public interface Event<T extends EventBase> {
    static <T extends EventBase> Event<T> create(Class<T> eventType) {
        return new EventImpl<>(eventType);
    }

    T doFire(T event);

    void subscribe(EventPriority priority, Consumer<T> listener);

    void unsubscribe(Consumer<T> listener);

    default void subscribe(Consumer<T> listener) {
        subscribe(EventPriority.NORMAL, listener);
    }
}
