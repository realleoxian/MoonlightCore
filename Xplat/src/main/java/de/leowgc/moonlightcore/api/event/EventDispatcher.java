package de.leowgc.moonlightcore.api.event;

import de.leowgc.moonlightcore.event.EventDispatcherImpl;

import java.util.function.Consumer;

public interface EventDispatcher {
    EventDispatcher INSTANCE = EventDispatcherImpl.INSTANCE;

    <T> void fire(Event<T> event, Consumer<T> output);
}
