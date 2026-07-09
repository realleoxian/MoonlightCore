package de.leoxian.moonlightcore.client.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.client.multiplayer.ClientLevel;

public final class ClientLevelTickEvents {
    public static final Event<Start> START = Event.create(Start.class, listeners -> level -> {
        for (final var listener : listeners) {
            listener.onLevelTickStart(level);
        }
    });
    public static final Event<End> END = Event.create(End.class, listeners -> level -> {
        for (final var listener : listeners) {
            listener.onLevelTickEnd(level);
        }
    });

    private ClientLevelTickEvents() {}

    @FunctionalInterface
    public interface Start {
        void onLevelTickStart(ClientLevel level);
    }

    @FunctionalInterface
    public interface End {
        void onLevelTickEnd(ClientLevel level);
    }
}
