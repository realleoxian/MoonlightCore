package de.leoxian.moonlightcore.event;

import de.leoxian.moonlightcore.api.event.Event;
import de.leoxian.moonlightcore.api.event.EventDispatcher;

import java.util.function.Consumer;

public enum EventDispatcherImpl implements EventDispatcher {
    INSTANCE
    ;


    @Override
    public <T> void fire(Event<T> event, Consumer<T> output) {
        try {
            for(T listener : ((EventImpl<T>) event).listeners().stream().map((listener) -> listener.listener).toList()) {
                output.accept(listener);
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
