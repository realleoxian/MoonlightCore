package de.leoxian.moonlightcore.api.client.event;

import de.leoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.Minecraft;

public final class ClientTickEvents {
    /**
     * @see Start#onStartClientTick(Minecraft)
     */
    public static final EventBus<Start> TICK_START = EventBus.create((listeners) -> (client) -> {
        for(ClientTickEvents.Start listener : listeners) {
            listener.onStartClientTick(client);
        }
    });
    /**
     * @see End#onEndClientTick(Minecraft)
     */
    public static final EventBus<End> TICK_END = EventBus.create((listeners) -> (client) -> {
        for(ClientTickEvents.End listener : listeners) {
            listener.onEndClientTick(client);
        }
    });

    private ClientTickEvents() {}

    public interface Start {
        /**
         * Invoked when a client tick is starting
         * @param client The client that its ticking
         */
        void onStartClientTick(Minecraft client);
    }

    public interface End {
        /**
         * Invoked after a client tick its processed
         * @param client The client that its ticking
         */
        void onEndClientTick(Minecraft client);
    }
}
