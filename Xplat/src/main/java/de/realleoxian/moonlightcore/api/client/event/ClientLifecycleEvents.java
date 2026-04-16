package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.Minecraft;

public final class ClientLifecycleEvents {
    public static final EventBus<Started> STARTED = EventBus.create(Started.class, (listeners) -> (client) -> {
       for(ClientLifecycleEvents.Started listener : listeners) {
           listener.onClientStarted(client);
       }
    });
    public static final EventBus<Stopping> STOPPING = EventBus.create(Stopping.class, (listeners) -> (client) -> {
        for(ClientLifecycleEvents.Stopping listener : listeners) {
            listener.onClientStopping(client);
        }
    });

    private ClientLifecycleEvents() {}

    public interface Started {
        void onClientStarted(Minecraft client);
    }

    public interface Stopping {
        void onClientStopping(Minecraft client);
    }
}
