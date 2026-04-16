package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.Minecraft;

public final class ClientTickEvents {
    public static final EventBus<Start> TICK_START = EventBus.create(Start.class, (listeners) -> (client) -> {
        for(ClientTickEvents.Start listener : listeners) {
            listener.onStartClientTick(client);
        }
    });
    public static final EventBus<End> TICK_END = EventBus.create(End.class, (listeners) -> (client) -> {
        for(ClientTickEvents.End listener : listeners) {
            listener.onEndClientTick(client);
        }
    });

    private ClientTickEvents() {}

    public interface Start {
        void onStartClientTick(Minecraft client);
    }

    public interface End {
        void onEndClientTick(Minecraft client);
    }
}
