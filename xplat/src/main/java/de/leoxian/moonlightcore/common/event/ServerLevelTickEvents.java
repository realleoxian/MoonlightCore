package de.leoxian.moonlightcore.common.event;

import de.leoxian.moonlightcore.common.event.base.Event;
import net.minecraft.server.level.ServerLevel;

public final class ServerLevelTickEvents {
    public static final Event<Start> START = Event.create(Start.class, listeners -> level -> {
       for (final var listener : listeners) {
           listener.onServerLevelTickStart(level);
       }
    });
    public static final Event<End> END = Event.create(End.class, listeners -> level -> {
        for (final var listener : listeners) {
            listener.onServerLevelTickEnd(level);
        }
    });

    private ServerLevelTickEvents() {}

    @FunctionalInterface
    public interface Start {
        void onServerLevelTickStart(ServerLevel level);
    }

    @FunctionalInterface
    public interface End {
        void onServerLevelTickEnd(ServerLevel level);
    }
}
