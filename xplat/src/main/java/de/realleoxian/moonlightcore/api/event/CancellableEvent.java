package de.realleoxian.moonlightcore.api.event;

public interface CancellableEvent {
    default void setCancelled(boolean cancelled) {
        ((EventBase) this).cancelled = cancelled;
    }

    default boolean isCancelled() {
        return ((EventBase) this).cancelled;
    }
}
