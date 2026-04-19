package de.realleoxian.moonlightcore.api.client.event;

import de.realleoxian.moonlightcore.api.event.EventBus;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientLevelTickEvents {
    public static final EventBus<Start> START = EventBus.create(Start.class, (listeners) -> (level) -> {
        for (Start listener : listeners) {
            listener.onClientLevelTickStart(level);
        }
    });
    public static final EventBus<End> END = EventBus.create(End.class, (listeners) -> (level) -> {
        for (End listener : listeners) {
            listener.onClientLevelTickEnd(level);
        }
    });

    private ClientLevelTickEvents() {}

    public interface Start {
        void onClientLevelTickStart(ClientLevel level);
    }

    public interface End {
        void onClientLevelTickEnd(ClientLevel level);
    }
}
