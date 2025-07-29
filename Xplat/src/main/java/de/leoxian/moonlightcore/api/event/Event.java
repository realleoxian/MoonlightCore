package de.leoxian.moonlightcore.api.event;

import de.leoxian.moonlightcore.event.EventImpl;

public interface Event<T> {
    static <T> Event<T> create() {
        return new EventImpl<>();
    }

    void subscribe(EventPriority priority, T listener);

    default void subscribe(T listener) {
        this.subscribe(EventPriority.NORMAL, listener);
    }
}
