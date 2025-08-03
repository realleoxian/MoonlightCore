package de.leowgc.moonlightcore.api.event.client;

import de.leowgc.moonlightcore.api.event.Event;

public interface ClientTickEvent {
    Event<ClientTickEvent> CLIENT_TICK = Event.create();

    void bootstrap(Phase phase);

    enum Phase {
        START,
        END;
    }
}
