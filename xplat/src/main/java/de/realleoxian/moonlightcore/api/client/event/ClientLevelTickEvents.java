package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientLevelTickEvents extends EventBase {
    public static final Event<ClientLevelTickEvents> START = Event.create(ClientLevelTickEvents.class);
    public static final Event<ClientLevelTickEvents> END = Event.create(ClientLevelTickEvents.class);

    public final ClientLevel level;

    public ClientLevelTickEvents(ClientLevel level) {
        this.level = level;
    }
}
