package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

public final class ClientTickEvents extends EventBase {
    public static final Event<ClientTickEvents> START = Event.create(ClientTickEvents.class);
    public static final Event<ClientTickEvents> END = Event.create(ClientTickEvents.class);

    public final Minecraft minecraft;

    @ApiStatus.Internal
    public ClientTickEvents(Minecraft minecraft) {
        this.minecraft = minecraft;
    }
}
