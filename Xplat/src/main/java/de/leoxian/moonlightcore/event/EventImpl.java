package de.leoxian.moonlightcore.event;

import com.google.common.collect.ImmutableList;
import de.leoxian.moonlightcore.api.event.Event;
import de.leoxian.moonlightcore.api.event.EventPriority;
import de.leoxian.moonlightcore.api.util.SortedLinkedList;

import java.util.Objects;

public final class EventImpl<T> implements Event<T> {
    private final SortedLinkedList<Listener<T>> listeners;

    public EventImpl() {
        this.listeners = new SortedLinkedList<>((a, b) -> b.priority.ordinal() - a.priority.ordinal());
    }

    @Override
    public void subscribe(EventPriority priority, T listener) {
        if(!this.listeners.contains(listener)) {
            this.listeners.add(new Listener<>(priority, listener));
        }
    }

    public ImmutableList<Listener<T>> listeners() {
        return ImmutableList.copyOf(this.listeners);
    }

    public static class Listener<T> {
        private final EventPriority priority;
        final T listener;

        private Listener(EventPriority priority, T listener) {
            this.priority  =priority;
            this.listener = listener;
        }

        @Override
        public boolean equals(Object obj) {
            if(this == obj) return true;
            if(obj instanceof Listener<?> that) {
                return Objects.equals(this.listener, that.listener);
            } else if (obj != null) {
                return obj.equals(this.listener);
            }

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.listener);
        }

        @Override
        public String toString() {
            return this.listener.toString() + " - " + this.priority;
        }
    }
}
