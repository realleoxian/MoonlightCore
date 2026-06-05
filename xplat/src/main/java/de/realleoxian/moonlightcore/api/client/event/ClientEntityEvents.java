package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public final class ClientEntityEvents extends EventBase {
    public static final Event<ClientEntityEvents> LOAD = Event.create(ClientEntityEvents.class);
    public static final Event<ClientEntityEvents> UNLOAD = Event.create(ClientEntityEvents.class);

    public final ClientLevel level;
    public final Entity entity;

    public ClientEntityEvents(ClientLevel level, Entity entity) {
        this.level = level;
        this.entity = entity;
    }
}
