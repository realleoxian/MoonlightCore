package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.Minecraft;

public final class ClientLifecycleEvents {
    /**
     * @see Started#onClientStarted(Minecraft)
     */
    public static final EventBus<Started> STARTED = EventBus.create(Started.class, (listeners) -> (client) -> {
       for(ClientLifecycleEvents.Started listener : listeners) {
           listener.onClientStarted(client);
       }
    });
    /**
     * @see Stopping#onClientStopping(Minecraft)
     */
    public static final EventBus<Stopping> STOPPING = EventBus.create(Stopping.class, (listeners) -> (client) -> {
        for(ClientLifecycleEvents.Stopping listener : listeners) {
            listener.onClientStopping(client);
        }
    });

    private ClientLifecycleEvents() {}

    public interface Started {
        /**
         * Invoked just before the minecraft client its about to tick for the first time
         * @param client The Minecraft client instance
         */
        void onClientStarted(Minecraft client);
    }

    public interface Stopping {
        /**
         * Invoked when the Minecraft client starts shutting down
         * @param client The Minecraft client instance
         */
        void onClientStopping(Minecraft client);
    }
}
