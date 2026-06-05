package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.Event;
import de.realleoxian.moonlightcore.api.event.EventBase;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.ApiStatus;

public final class ClientLifecycleEvents extends EventBase {
    public static final Event<ClientLifecycleEvents> STARTED = Event.create(ClientLifecycleEvents.class);
    public static final Event<ClientLifecycleEvents> STOPPING = Event.create(ClientLifecycleEvents.class);

    public final Minecraft minecraft;

    @ApiStatus.Internal
    public ClientLifecycleEvents(Minecraft minecraft) {
        this.minecraft = minecraft;
    }
}
