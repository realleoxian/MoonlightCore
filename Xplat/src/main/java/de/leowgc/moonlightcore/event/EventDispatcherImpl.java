package de.leowgc.moonlightcore.event;

import de.leowgc.moonlightcore.api.event.Event;
import de.leowgc.moonlightcore.api.event.EventDispatcher;
import de.leowgc.moonlightcore.api.event.EventPriority;

import java.util.function.Consumer;

public enum EventDispatcherImpl implements EventDispatcher {
    INSTANCE
    ;

    @Override
    public <T> void fire(Event<T> event, Consumer<T> output) {
        try {
            for(EventPriority priority : EventPriority.values()) {
                for(T listener : ((EventImpl<T>) event).listeners(priority)) {
                    output.accept(listener);
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
