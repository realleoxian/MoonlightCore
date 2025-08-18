package de.leowgc.moonlightcore.event;

import com.google.common.collect.ImmutableList;
import de.leowgc.moonlightcore.api.event.Event;
import de.leowgc.moonlightcore.api.event.EventPriority;

import java.util.*;

public final class EventImpl<T> implements Event<T> {
    private final Map<EventPriority, List<T>> listeners = new HashMap<>();

    public EventImpl() {
        for(EventPriority priority : EventPriority.values()) {
            this.listeners.put(priority, new ArrayList<>());
        }
    }

    @Override
    public void subscribe(EventPriority priority, T listener) {
        this.listeners.get(priority).add(listener);
    }

    public List<T> listeners(EventPriority priority) {
        return ImmutableList.copyOf(this.listeners.get(priority));
    }
}
