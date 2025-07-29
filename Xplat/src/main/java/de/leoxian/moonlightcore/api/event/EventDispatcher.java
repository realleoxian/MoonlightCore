package de.leoxian.moonlightcore.api.event;

import de.leoxian.moonlightcore.event.EventDispatcherImpl;

import java.util.function.Consumer;

public interface EventDispatcher {
    EventDispatcher INSTANCE = EventDispatcherImpl.INSTANCE;

    <T> void fire(Event<T> event, Consumer<T> output);
}
