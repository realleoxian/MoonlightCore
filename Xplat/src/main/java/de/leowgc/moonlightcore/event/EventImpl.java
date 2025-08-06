package de.leowgc.moonlightcore.event;

import com.google.common.collect.ImmutableList;
import de.leowgc.moonlightcore.api.event.Event;
import de.leowgc.moonlightcore.api.event.EventPriority;

import java.util.*;

public final class EventImpl<T> implements Event<T> {
    private final Map<EventPriority, List<T>> listeners;

    public EventImpl() {
        this.listeners = new EnumMap<>(EventPriority.class);

        for(EventPriority priority : EventPriority.values()) {
            this.listeners.put(priority, new ArrayList<>());
        }
    }

    @Override
    public void subscribe(EventPriority priority, T listener) {
        if(!this.listeners.get(priority).contains(listener)) {
            this.listeners.get(priority).add(listener);
        }
    }

    public ImmutableList<T> listeners(EventPriority priority) {
        return ImmutableList.copyOf(this.listeners.get(priority));
    }

//    public static class Listener<T> {
//        private final EventPriority priority;
//        final T listener;
//
//        private Listener(EventPriority priority, T listener) {
//            this.priority  =priority;
//            this.listener = listener;
//        }
//
//        @Override
//        public boolean equals(Object obj) {
//            if(this == obj) return true;
//            if(obj instanceof Listener<?> that) {
//                return Objects.equals(this.listener, that.listener);
//            } else if (obj != null) {
//                return obj.equals(this.listener);
//            }
//
//            return false;
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(this.listener);
//        }
//
//        @Override
//        public String toString() {
//            return this.listener.toString() + " - " + this.priority;
//        }
//    }
}
